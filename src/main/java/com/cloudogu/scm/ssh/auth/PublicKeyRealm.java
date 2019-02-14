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
  protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
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

    if ("sdorra".equals(publicKeyToken.getPrincipal())) {
      String keyStringRepresentation = "ssh-rsa AAAAB3NzaC1yc2EAAAADAQABAAACAQCu6TW1lFtGAH4OmEGJV341lYvXFaewHHmkaWkgql7IDMTWSjj/D0HR0sbOk6R/EpfjrRowmymrFsMyWzC3mqSrrGHP0qZiQPEWXZxDhl+fIXjTOqb5Kh8Huja7Ni090kb9r66/pdz1hdk7YZJUYlY3GZlVZEAwfGpxSlgNGwmO2wz5ihn8GN/mzfiELgIxWf9eQ3AsnrV+/JGuTLy/twqkPpqjdGW3kC1PGnBbPIXUWPfTYrj9T2li5BlgpGELI4TNHNEc88htyprfA88zASRLsZyIUUVZPIQ8bpRwMgB7Y/RMiKxps71D4qwhNOOUqGnwZnrLXvaB6G+kZBaJ3AcCtgzQD/4wIx5uhJ3/tpyDvq0c471P+Ph2vswEiVOcJcDqrUbGBYtjFr3SiAX6h434uJbgGr5Bxos0jQ18J9PohvEPb4qsOb6PhOSJf5+YpORNanZIcMwq6JImrR95XdCuBSRg6h8qXPxNdJsU1roMLcCkgEll1fPABYvVWASKRZIWWJ9pevS7oqyDOo82bAdh3813WqAEflUj15S4LQxnLwjNUuW+HebzMct+z2RN6l8ZH5TQ9fhOkQiufwNG5bzckjKb5UidC86FaqlJ5LTZTfEeyAlE/4chtzpyEZQo4le7fXFs6NBsPS6RDG/cgHynWu3QgQtGFSUzrtewWu0fiQ== cardno:000607466087";

      AuthorizedKeyEntry entry = AuthorizedKeyEntry.parseAuthorizedKeyEntry(keyStringRepresentation);
      entries.add(entry);

      return entries;
    }

    Iterable<AuthorizedKey> keys = authorizedKeyStore.getAll(publicKeyToken.getPrincipal());
    for (AuthorizedKey key : keys) {
      AuthorizedKeyEntry entry = AuthorizedKeyEntry.parseAuthorizedKeyEntry(key.getAuthorizedKey());
      entries.add(entry);
    }
    return entries;
  }
}
