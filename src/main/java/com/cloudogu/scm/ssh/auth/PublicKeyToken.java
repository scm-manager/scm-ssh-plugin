package com.cloudogu.scm.ssh.auth;

import org.apache.shiro.authc.AuthenticationToken;
import org.apache.sshd.server.session.ServerSession;

import java.security.PublicKey;

class PublicKeyToken implements AuthenticationToken {

  private final ServerSession session;
  private final String principal;
  private final PublicKey publicKey;

  PublicKeyToken(ServerSession session, String principal, PublicKey publicKey) {
    this.session = session;
    this.principal = principal;
    this.publicKey = publicKey;
  }

  @Override
  public String getPrincipal() {
    return principal;
  }

  @Override
  public PublicKey getCredentials() {
    return publicKey;
  }

  ServerSession getSession() {
    return session;
  }
}
