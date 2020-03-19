/**
 * MIT License
 *
 * Copyright (c) 2020-present Cloudogu GmbH and Contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
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

import javax.inject.Inject;
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
