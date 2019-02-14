package com.cloudogu.scm.ssh.auth;

import com.cloudogu.scm.ssh.SshServerConfigurator;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.sshd.server.SshServer;
import org.apache.sshd.server.auth.password.PasswordAuthenticator;
import org.apache.sshd.server.session.ServerSession;

public class ShiroPasswordAuthenticator implements PasswordAuthenticator, SshServerConfigurator {
  @Override
  public boolean authenticate(String username, String password, ServerSession session) {
    return ShiroAuthenticator.authenticate(new UsernamePasswordToken(username, password), session);
  }

  @Override
  public void configure(SshServer server) {
    server.setPasswordAuthenticator(this);
  }
}
