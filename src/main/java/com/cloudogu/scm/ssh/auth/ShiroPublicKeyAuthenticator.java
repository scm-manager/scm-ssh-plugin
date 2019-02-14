package com.cloudogu.scm.ssh.auth;

import com.cloudogu.scm.ssh.SshServerConfigurator;
import org.apache.sshd.server.SshServer;
import org.apache.sshd.server.auth.AsyncAuthException;
import org.apache.sshd.server.auth.pubkey.PublickeyAuthenticator;
import org.apache.sshd.server.session.ServerSession;

import java.security.PublicKey;

public class ShiroPublicKeyAuthenticator implements PublickeyAuthenticator, SshServerConfigurator {
  @Override
  public void configure(SshServer server) {
    server.setPublickeyAuthenticator(this);
  }

  @Override
  public boolean authenticate(String username, PublicKey key, ServerSession session) throws AsyncAuthException {
    return ShiroAuthenticator.authenticate(new PublicKeyToken(session, username, key), session);
  }
}
