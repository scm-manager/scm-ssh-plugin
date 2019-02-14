package com.cloudogu.scm.ssh.auth;

import com.cloudogu.scm.ssh.Attributes;
import com.cloudogu.scm.ssh.SshServerConfigurator;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.apache.sshd.server.SshServer;
import org.apache.sshd.server.auth.password.PasswordAuthenticator;
import org.apache.sshd.server.session.ServerSession;

public class ShiroPasswordAuthenticator implements PasswordAuthenticator, SshServerConfigurator {
  @Override
  public boolean authenticate(String username, String password, ServerSession session) {
    Subject subject = SecurityUtils.getSubject();
    try {
      subject.login(new UsernamePasswordToken(username, password));
      session.setAttribute(Attributes.SUBJECT, subject);
      return true;
    } catch (AuthenticationException e) {
      return false;
    }
  }

  @Override
  public void configure(SshServer server) {
    server.setPasswordAuthenticator(this);
  }
}
