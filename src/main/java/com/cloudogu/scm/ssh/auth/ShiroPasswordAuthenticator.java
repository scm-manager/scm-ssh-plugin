package com.cloudogu.scm.ssh.auth;

import com.cloudogu.scm.ssh.ConfigStore;
import com.cloudogu.scm.ssh.Configuration;
import com.cloudogu.scm.ssh.SshServerConfigurator;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.sshd.server.SshServer;
import org.apache.sshd.server.auth.password.PasswordAuthenticator;
import org.apache.sshd.server.session.ServerSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;

public class ShiroPasswordAuthenticator implements PasswordAuthenticator, SshServerConfigurator {

  private static final Logger LOG = LoggerFactory.getLogger(ShiroPasswordAuthenticator.class);

  private final ConfigStore configStore;

  @Inject
  public ShiroPasswordAuthenticator(ConfigStore configStore) {
    this.configStore = configStore;
  }

  @Override
  public boolean authenticate(String username, String password, ServerSession session) {
    if (!isPasswordAuthenticationDisabled()) {
      return ShiroAuthenticator.authenticate(new UsernamePasswordToken(username, password), session);
    }
    return false;
  }

  private boolean isPasswordAuthenticationDisabled() {
    Configuration configuration = configStore.getConfiguration();
    return configuration.isDisablePasswordAuthentication();
  }

  @Override
  public void configure(SshServer server) {
    if (isPasswordAuthenticationDisabled()) {
      LOG.info("skip enabling ssh password authentication, because it is disabled by configuration");
    } else {
      LOG.debug("enable ssh password authentication");
      server.setPasswordAuthenticator(this);
    }
  }
}
