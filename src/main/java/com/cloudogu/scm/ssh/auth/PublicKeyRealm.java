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

import com.google.common.annotations.VisibleForTesting;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.credential.AllowAllCredentialsMatcher;
import org.apache.shiro.realm.AuthenticatingRealm;
import org.apache.sshd.common.config.keys.AuthorizedKeyEntry;
import org.apache.sshd.server.auth.pubkey.PublickeyAuthenticator;
import org.apache.sshd.server.session.ServerSession;
import sonia.scm.plugin.Extension;
import sonia.scm.security.DAORealmHelper;
import sonia.scm.security.DAORealmHelperFactory;

import jakarta.inject.Inject;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;

/**
 * TODO check if we should only apply it to the SshSecurityManager
 */
@Extension
public class PublicKeyRealm extends AuthenticatingRealm {

  @VisibleForTesting
  static final String REALM = "PublicKeyRealm";

  private final DAORealmHelper daoRealmHelper;
  private final AuthorizedKeyStore authorizedKeyStore;

  @Inject
  public PublicKeyRealm(DAORealmHelperFactory daoRealmHelperFactory, AuthorizedKeyStore authorizedKeyStore) {
    this.daoRealmHelper = daoRealmHelperFactory.create(REALM);
    this.authorizedKeyStore = authorizedKeyStore;

    setAuthenticationTokenClass(PublicKeyToken.class);
    setCredentialsMatcher(new AllowAllCredentialsMatcher());
  }

  @Override
  protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) {
    PublicKeyToken publicKeyToken = (PublicKeyToken) token;
    if (isAuthenticated(publicKeyToken)) {
      return daoRealmHelper.authenticationInfoBuilder(publicKeyToken.getPrincipal()).build();
    } else {
      throw new AuthenticationException("authentication failed");
    }
  }

  private boolean isAuthenticated(PublicKeyToken publicKeyToken) {
    List<AuthorizedKeyEntry> entries = loadAuthorizedKeys(publicKeyToken);
    PublickeyAuthenticator authenticator = createAuthenticator(publicKeyToken.getSession(), entries);

    return authenticator.authenticate(
      publicKeyToken.getPrincipal(), publicKeyToken.getCredentials(), publicKeyToken.getSession()
    );
  }

  private PublickeyAuthenticator createAuthenticator(ServerSession session, List<AuthorizedKeyEntry> entries) {
    try {
      return PublickeyAuthenticator.fromAuthorizedEntries(REALM, session, entries, null);
    } catch (IOException | GeneralSecurityException e) {
      throw new AuthenticationException("failed to create public key authenticator", e);
    }
  }

  private List<AuthorizedKeyEntry> loadAuthorizedKeys(PublicKeyToken publicKeyToken) {
    List<AuthorizedKeyEntry> entries = new ArrayList<>();
    Iterable<AuthorizedKey> keys = authorizedKeyStore.getAllWithoutPermissionCheck(publicKeyToken.getPrincipal());
    for (AuthorizedKey key : keys) {
      AuthorizedKeyEntry entry = AuthorizedKeyEntry.parseAuthorizedKeyEntry(key.getRaw());
      entries.add(entry);
    }
    return entries;
  }
}
