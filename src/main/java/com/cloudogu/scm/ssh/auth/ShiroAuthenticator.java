package com.cloudogu.scm.ssh.auth;

import com.cloudogu.scm.ssh.Attributes;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.subject.Subject;
import org.apache.sshd.server.session.ServerSession;

final class ShiroAuthenticator {

  private ShiroAuthenticator() {
  }

  static boolean authenticate(AuthenticationToken token, ServerSession session) {
    Subject subject = SecurityUtils.getSubject();
    try {
      subject.login(token);
      session.setAttribute(Attributes.SUBJECT, subject);
      return true;
    } catch (AuthenticationException e) {
      return false;
    }
  }
}
