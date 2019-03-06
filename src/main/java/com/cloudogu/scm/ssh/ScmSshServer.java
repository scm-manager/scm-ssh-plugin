package com.cloudogu.scm.ssh;

import com.cloudogu.scm.ssh.auth.SshSecurityManager;
import com.github.legman.Subscribe;
import com.google.common.annotations.VisibleForTesting;
import org.apache.shiro.util.ThreadContext;
import org.apache.sshd.server.SshServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.io.IOException;
import java.util.Set;

public class ScmSshServer {

  private static final Logger LOG = LoggerFactory.getLogger(ScmSshServer.class);

  private SshServer sshd;
  private final SshSecurityManager securityManager;
  private final Set<SshServerConfigurator> configurators;

  @Inject
  public ScmSshServer(SshSecurityManager securityManager, Set<SshServerConfigurator> configurators) {
    this.securityManager = securityManager;
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
      ThreadContext.bind( securityManager );

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
    if (configChangedEvent.getOldItem().getPort() != configChangedEvent.getItem().getPort()) {
      LOG.trace("configuration for ssh server changed");
      stop();
      sshd = createDefaultServer();
      applyConfigurators();
      start();
    }
  }

  @VisibleForTesting
  void runInThread(Runnable r) {
    new Thread(r).start();
  }

  @VisibleForTesting
  SshServer createDefaultServer() {
    return SshServer.setUpDefaultServer();
  }
}
