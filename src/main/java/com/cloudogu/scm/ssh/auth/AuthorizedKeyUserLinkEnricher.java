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

import org.apache.shiro.subject.Subject;
import sonia.scm.api.v2.resources.Enrich;
import sonia.scm.api.v2.resources.HalEnricherContext;
import sonia.scm.api.v2.resources.ScmPathInfoStore;
import sonia.scm.plugin.Extension;
import sonia.scm.user.User;

import jakarta.inject.Inject;
import jakarta.inject.Provider;

@Extension
@Enrich(User.class)
public class AuthorizedKeyUserLinkEnricher extends AbstractAuthorizedKeyLinkEnricher {

  @Inject
  public AuthorizedKeyUserLinkEnricher(Provider<ScmPathInfoStore> scmPathInfoStore) {
    super(scmPathInfoStore);
  }

  @Override
  protected String getUsername(HalEnricherContext context, Subject subject) {
    User user = context.oneRequireByType(User.class);
    return user.getName();
  }
}
