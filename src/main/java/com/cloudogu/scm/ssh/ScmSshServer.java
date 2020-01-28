package com.cloudogu.scm.ssh;

import com.cloudogu.scm.ssh.auth.SshSecurityManager;
import com.github.legman.Subscribe;
import com.google.common.annotations.VisibleForTesting;
import org.apache.shiro.util.ThreadContext;
import org.apache.sshd.server.SshServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Provider;
import java.io.IOException;
import java.util.Set;

import static sun.print.CUPSPrinter.getPort;

public class ScmSshServer {

  private static final Logger LOG = LoggerFactory.getLogger(ScmSshServer.class);

  private SshServer sshd;
  private final Provider<SshSecurityManager> securityManagerProvider;
  private final Set<SshServerConfigurator> configurators;

  @Inject
  public ScmSshServer(Provider<SshSecurityManager> securityManagerProvider, Set<SshServerConfigurator> configurators) {
    this.securityManagerProvider = securityManagerProvider;
    this.configurators = configurators;

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
    return SshServer.setUpDefaultServer();
  }
}
