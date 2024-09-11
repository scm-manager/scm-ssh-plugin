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
