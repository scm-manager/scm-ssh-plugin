/*
 * Copyright (c) 2020 - present Cloudogu GmbH
 *
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, version 3.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see https://www.gnu.org/licenses/.
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

import jakarta.inject.Inject;
import jakarta.inject.Provider;
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
      || hasDisabledPasswordAuthenticationChanged(oldConfiguration, newConfiguration)
      || hasAlgorithmChanged(oldConfiguration, newConfiguration);
  }

  private boolean hasAlgorithmChanged(Configuration oldConfiguration, Configuration newConfiguration) {
    return oldConfiguration.getAlgorithm() != newConfiguration.getAlgorithm();
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
