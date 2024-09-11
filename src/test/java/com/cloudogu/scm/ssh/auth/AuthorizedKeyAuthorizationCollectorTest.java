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

import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.subject.SimplePrincipalCollection;
import org.junit.jupiter.api.Test;
import sonia.scm.SCMContext;

import static org.assertj.core.api.Assertions.assertThat;

class AuthorizedKeyAuthorizationCollectorTest {

  private final AuthorizedKeyAuthorizationCollector collector = new AuthorizedKeyAuthorizationCollector();

  @Test
  void shouldReturnDefaultPermissions() {
    SimplePrincipalCollection collection = new SimplePrincipalCollection("trillian", "test");

    AuthorizationInfo info = collector.collect(collection);

    assertThat(info.getStringPermissions()).containsOnly("user:readAuthorizedKeys,writeAuthorizedKeys:trillian");
  }

  @Test
  void shouldNotAddDefaultPermissionsIfUserIsAnonymous() {
    SimplePrincipalCollection collection = new SimplePrincipalCollection(SCMContext.USER_ANONYMOUS, "test");

    AuthorizationInfo info = collector.collect(collection);

    assertThat(info.getStringPermissions()).isNullOrEmpty();
  }

}
