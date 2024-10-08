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

import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.sshd.common.config.keys.AuthorizedKeyEntry;
import org.apache.sshd.common.config.keys.impl.RSAPublicKeyDecoder;
import org.apache.sshd.server.session.ServerSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import sonia.scm.security.DAORealmHelper;
import sonia.scm.security.DAORealmHelperFactory;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PublicKeyRealmTest {

  private static final String KEY_ONE = "ssh-rsa AAAAB3NzaC1yc2EAAAADAQABAAAAgQDRtRfByMYtBLow0bgzoEQKyz3YCJRxTaadub" +
    "DCBv8pBTd7WbU+m/DcsS6tKl3PxBxLP94be6lamlIGQwHeI4GeyN5xoT5Gv4guKdvsKyi3aoR3UpqtcjA74WBq9cGPB5gg+/Fxr/IChsvP" +
    "S/VCEwIo49LRsRMIgxOy0TcKdsCNkw== tricia@heartOfGold";

  private static final String KEY_TWO = "ssh-rsa AAAAB3NzaC1yc2EAAAADAQABAAAAgQCsyCMlHm2ZyKHcwF3WqLk8FBp0tQUUmqhYvs" +
    "5+IQ92jE4a/ltU4SBG3XCS4nINv5GAbcnPVddEFNYpWT3xrwcL9ICjqGPKcTIT3AS2StZGLj3fEepXfA+rZoESlq/8deo5j9YkPpzFgeiP" +
    "3Dar6cMMvr79ANyLRK9HYwc1eWRjDQ== tricia@heartOfGold";

  private static final String KEY_THREE = "ssh-rsa AAAAB3NzaC1yc2EAAAADAQABAAAAgQDwX2aNOMFGTAGCCU4seXuwSoXv0B9gxklp" +
    "yihizsOabe0EGKhAPVwjjPz99I4nzJ+cSqY63aALzpWLRzgMgcMsDTX+rks0JLY9QRDLB/p9MtNE1DGBnv4S/zPYHEU92rZYf9WzXqUj1R" +
    "PHFT6wYniWGgBAhPRTfyF7ztxqTORYWw== tricia@heartOfGold";

  @Mock
  private DAORealmHelperFactory daoRealmHelperFactory;

  @Mock
  private DAORealmHelper daoRealmHelper;

  @Mock
  private AuthorizedKeyStore authorizedKeyStore;


  private PublicKeyRealm realm;

  @Mock
  private ServerSession session;

  @BeforeEach
  void setUpObjectUnderTest() {
    when(daoRealmHelperFactory.create(PublicKeyRealm.REALM)).thenReturn(daoRealmHelper);
    realm = new PublicKeyRealm(daoRealmHelperFactory, authorizedKeyStore);
  }

  @Test
  void shouldFailWithoutKeys() throws IOException, GeneralSecurityException {
    PublicKeyToken token = createTokenForTrillian("trillian", KEY_ONE);
    assertThrows(AuthenticationException.class, () -> realm.doGetAuthenticationInfo(token));
  }

  @Test
  void shouldFailWithoutMatchingKey() throws IOException, GeneralSecurityException {
    mockKeyStore("trillian", KEY_ONE, KEY_TWO);

    PublicKeyToken token = createTokenForTrillian("trillian", KEY_THREE);

    assertThrows(AuthenticationException.class, () -> realm.doGetAuthenticationInfo(token));
  }

  @Test
  void shouldDoAuthentication() throws IOException, GeneralSecurityException {
    AuthenticationInfo expected = mockDaoRealm("trillian");
    mockKeyStore("trillian", KEY_ONE);

    PublicKeyToken token = createTokenForTrillian("trillian", KEY_ONE);
    AuthenticationInfo authenticationInfo = realm.doGetAuthenticationInfo(token);

    assertThat(authenticationInfo).isSameAs(expected);
  }

  @Test
  void shouldDoAuthenticationWithMultipleKeys() throws IOException, GeneralSecurityException {
    AuthenticationInfo expected = mockDaoRealm("trillian");
    mockKeyStore("trillian", KEY_ONE, KEY_TWO, KEY_THREE);

    PublicKeyToken token = createTokenForTrillian("trillian", KEY_TWO);
    AuthenticationInfo authenticationInfo = realm.doGetAuthenticationInfo(token);

    assertThat(authenticationInfo).isSameAs(expected);
  }

  private void mockKeyStore(String username, String... keys) {
    List<AuthorizedKey> list = new ArrayList<>();
    for (String key : keys) {
      AuthorizedKey authorizedKey = new AuthorizedKey();
      authorizedKey.setRaw(key);
      list.add(authorizedKey);
    }

    when(authorizedKeyStore.getAllWithoutPermissionCheck(username)).thenReturn(list);
  }

  private AuthenticationInfo mockDaoRealm(String username) {
    DAORealmHelper.AuthenticationInfoBuilder builder = mock(DAORealmHelper.AuthenticationInfoBuilder.class);
    when(daoRealmHelper.authenticationInfoBuilder(username)).thenReturn(builder);
    SimpleAuthenticationInfo authenticationInfo = new SimpleAuthenticationInfo();
    when(builder.build()).thenReturn(authenticationInfo);
    return authenticationInfo;
  }

  private PublicKeyToken createTokenForTrillian(String username, String key) throws IOException, GeneralSecurityException {
    PublicKey publicKey = createPublicKey(key);
    return new PublicKeyToken(session, username, publicKey);
  }


  private PublicKey createPublicKey(String raw) throws IOException, GeneralSecurityException {
    AuthorizedKeyEntry entry = AuthorizedKeyEntry.parseAuthorizedKeyEntry(raw);
    return entry.resolvePublicKey(null, RSAPublicKeyDecoder.INSTANCE);
  }

}
