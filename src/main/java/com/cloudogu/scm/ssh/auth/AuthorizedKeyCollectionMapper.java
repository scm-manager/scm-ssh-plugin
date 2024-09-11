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

import de.otto.edison.hal.Embedded;
import de.otto.edison.hal.HalRepresentation;
import de.otto.edison.hal.Link;
import de.otto.edison.hal.Links;
import sonia.scm.api.v2.resources.LinkBuilder;
import sonia.scm.api.v2.resources.ScmPathInfoStore;

import jakarta.inject.Inject;
import jakarta.inject.Provider;
import java.util.List;
import java.util.stream.Collectors;

import static de.otto.edison.hal.Links.linkingTo;

public class AuthorizedKeyCollectionMapper {

  private final Provider<ScmPathInfoStore> scmPathInfoStore;
  private final AuthorizedKeyMapper mapper;

  @Inject
  public AuthorizedKeyCollectionMapper(Provider<ScmPathInfoStore> scmPathInfoStore, AuthorizedKeyMapper mapper) {
    this.scmPathInfoStore = scmPathInfoStore;
    this.mapper = mapper;
  }

  HalRepresentation map(String username, List<AuthorizedKey> keys) {
    List<AuthorizedKeyDto> dtos = keys.stream()
      .map(key -> mapper.map(username, key))
      .collect(Collectors.toList());

    Links.Builder builder = linkingTo();

    String self = selfLink(username);
    builder.self(self);

    if (hasCreatePermissions(username)) {
      String create = createLink(username);
      builder.single(Link.link("create", create));
    }

    return new HalRepresentation(builder.build(), Embedded.embedded("keys", dtos));
  }

  private boolean hasCreatePermissions(String username) {
    return Permissions.isPermittedWriteAuthorizedKeys(username);
  }

  private String createLink(String username) {
    return new LinkBuilder(scmPathInfoStore.get().get(), AuthorizedKeyResource.class)
        .method("create")
        .parameters(username)
        .href();
  }

  private String selfLink(String username) {
    return new LinkBuilder(scmPathInfoStore.get().get(), AuthorizedKeyResource.class)
      .method("findAll")
      .parameters(username)
      .href();
  }
}
