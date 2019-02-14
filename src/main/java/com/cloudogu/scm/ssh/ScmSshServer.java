package com.cloudogu.scm.ssh;

import com.cloudogu.scm.ssh.auth.SshSecurityManager;
import org.apache.shiro.util.ThreadContext;
import org.apache.sshd.server.SshServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.io.IOException;
import java.util.Set;

public class ScmSshServer {

  private static final Logger LOG = LoggerFactory.getLogger(ScmSshServer.class);

  private final SshServer sshd;
  private final SshSecurityManager securityManager;

  @Inject
  public ScmSshServer(SshSecurityManager securityManager, Set<SshServerConfigurator> configurators) {
    this.securityManager = securityManager;
    sshd = SshServer.setUpDefaultServer();

    for (SshServerConfigurator configurator : configurators) {
      configurator.configure(sshd);
    }
  }

  void start() {
    LOG.info("start ssh server");

    // start the sshd in a separate thread,
    // in order to bind a security manager to ssh threads only
    new Thread(() -> {
      ThreadContext.bind( securityManager );

      try {
        sshd.start();
      } catch (IOException e) {
        LOG.error("failed to start ");
      }


    }).start();
  }

  void stop() {
    LOG.info("stop ssh server");
    try {
      sshd.stop();
    } catch (IOException ex) {
      LOG.warn("failed to stop ssh server");
    }
  }

}
