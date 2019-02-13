package com.cloudogu.scm.ssh;

import com.cloudogu.scm.ssh.sample.SampleCommandFactory;
import org.apache.shiro.util.ThreadContext;
import org.apache.sshd.server.SshServer;
import org.apache.sshd.server.keyprovider.SimpleGeneratorHostKeyProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.io.IOException;
import java.nio.file.Paths;

public class ScmSshServer {

  private static final Logger LOG = LoggerFactory.getLogger(ScmSshServer.class);

  private final SshServer sshd;
  private final SshSecurityManager securityManager;

  @Inject
  public ScmSshServer(SshSecurityManager securityManager, Configuration configuration, ShiroPasswordAuthenticator passwordAuthenticator, SampleCommandFactory commandFactory) {
    this.securityManager = securityManager;
    sshd = SshServer.setUpDefaultServer();
    sshd.setScheduledExecutorService(ExecutorFactory.create(), true);

    sshd.setPort(configuration.getPort());
    sshd.setKeyPairProvider(new SimpleGeneratorHostKeyProvider(Paths.get("hostkey.ser")));

    sshd.setPasswordAuthenticator(passwordAuthenticator);
    sshd.setCommandFactory(commandFactory);
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
