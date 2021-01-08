/*
 * MIT License
 *
 * Copyright (c) 2020-present Cloudogu GmbH and Contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.cloudogu.scm.ssh;

import com.cloudogu.scm.ssh.auth.SshSecurityManager;
import com.github.legman.Subscribe;
import com.google.common.annotations.VisibleForTesting;
import org.apache.shiro.util.ThreadContext;
import org.apache.sshd.server.SshServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sonia.scm.plugin.PluginLoader;

import javax.inject.Inject;
import javax.inject.Provider;
import java.io.IOException;
import java.util.Set;

public class ScmSshServer {

  private static final Logger LOG = LoggerFactory.getLogger(ScmSshServer.class);

  private SshServer sshd;
  private final Provider<SshSecurityManager> securityManagerProvider;
  private final Set<SshServerConfigurator> configurators;
  private final PluginLoader pluginLoader;

  @Inject
  public ScmSshServer(Provider<SshSecurityManager> securityManagerProvider, Set<SshServerConfigurator> configurators, PluginLoader pluginLoader) {
    this.securityManagerProvider = securityManagerProvider;
    this.configurators = configurators;
    this.pluginLoader = pluginLoader;

    sshd = createDefaultServer();

    applyConfigurators();
  }

  private void applyConfigurators() {
    for (SshServerConfigurator configurator : configurators) {
      configurator.configure(sshd);
    }
  }

  void start() {
    LOG.info("start ssh server");

    // start the sshd in a separate thread,
    // in order to bind a security manager to ssh threads only
    runInThread(() -> {
      ThreadContext.bind(securityManagerProvider.get());

      try {
        sshd.start();
      } catch (Exception e) {
        LOG.error("failed to start ssh server", e);
      }

    });
  }

  void stop() {
    LOG.info("stop ssh server");
    try {
      sshd.stop();
    } catch (IOException e) {
      LOG.warn("failed to stop ssh server", e);
    }
  }

  @Subscribe
  public void configurationChanged(ConfigChangedEvent configChangedEvent) {
    if (shouldRestartServer(configChangedEvent)) {
      LOG.trace("configuration for ssh server changed");
      stop();
      sshd = createDefaultServer();
      applyConfigurators();
      start();
    }
  }

  private boolean shouldRestartServer(ConfigChangedEvent configChangedEvent) {
    Configuration oldConfiguration = configChangedEvent.getOldConfiguration();
    Configuration newConfiguration = configChangedEvent.getNewConfiguration();
    return hasPortChanged(oldConfiguration, newConfiguration)
      || hasDisabledPasswordAuthenticationChanged(oldConfiguration, newConfiguration);
  }

  private boolean hasDisabledPasswordAuthenticationChanged(Configuration oldConfiguration, Configuration newConfiguration) {
    return oldConfiguration.isDisablePasswordAuthentication() != newConfiguration.isDisablePasswordAuthentication();
  }

  private boolean hasPortChanged(Configuration oldConfiguration, Configuration newConfiguration) {
    return oldConfiguration.getPort() != newConfiguration.getPort();
  }

  @VisibleForTesting
  void runInThread(Runnable r) {
    new Thread(r, "ScmSshServer").start();
  }

  @VisibleForTesting
  SshServer createDefaultServer() {
    // we have to use the uber classloader,
    // without sshd does not find net.i2p.crypto security provider
    ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
    try {
      Thread.currentThread().setContextClassLoader(pluginLoader.getUberClassLoader());
      return SshServer.setUpDefaultServer();
    } finally {
      Thread.currentThread().setContextClassLoader(classLoader);
    }
  }

  @VisibleForTesting
  SshServer getServer() {
    return sshd;
  }
}
