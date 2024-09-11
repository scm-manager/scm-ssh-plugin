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
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.subject.PrincipalCollection;
import sonia.scm.plugin.Extension;
import sonia.scm.security.Authentications;
import sonia.scm.security.AuthorizationCollector;

@Extension
public class AuthorizedKeyAuthorizationCollector implements AuthorizationCollector {

  @Override
  public AuthorizationInfo collect(PrincipalCollection principalCollection) {
    SimpleAuthorizationInfo authorizationInfo = new SimpleAuthorizationInfo();

    String username = principalCollection.getPrimaryPrincipal().toString();
    if (!Authentications.isSubjectAnonymous(username)) {
      authorizationInfo.addStringPermission(Permissions.readAndWriteAuthorizedKeys(username));
    }

    return authorizationInfo;
  }

}
