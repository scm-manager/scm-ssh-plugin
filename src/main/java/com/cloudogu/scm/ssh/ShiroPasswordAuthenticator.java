package com.cloudogu.scm.ssh;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.apache.sshd.server.auth.password.PasswordAuthenticator;
import org.apache.sshd.server.session.ServerSession;

public class ShiroPasswordAuthenticator implements PasswordAuthenticator {
  @Override
  public boolean authenticate(String username, String password, ServerSession session) {
    Subject subject = SecurityUtils.getSubject();
    try {
      subject.login(new UsernamePasswordToken(username, password));
      return true;
    } catch (AuthenticationException e) {
      return false;
    }
  }
}
