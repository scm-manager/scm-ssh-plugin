/*
 * Copyright (c) 2020 - present Cloudogu GmbH
 *
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, version 3.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see https://www.gnu.org/licenses/.
 */

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
