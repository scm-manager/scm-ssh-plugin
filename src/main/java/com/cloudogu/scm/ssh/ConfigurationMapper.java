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

package com.cloudogu.scm.ssh;

import de.otto.edison.hal.Links;
import org.mapstruct.Mapper;
import org.mapstruct.ObjectFactory;
import sonia.scm.api.v2.resources.LinkBuilder;
import sonia.scm.api.v2.resources.ScmPathInfoStore;
import sonia.scm.config.ConfigurationPermissions;

import jakarta.inject.Inject;

import static de.otto.edison.hal.Link.link;
import static de.otto.edison.hal.Links.linkingTo;

@Mapper
public abstract class ConfigurationMapper {

  @Inject
  private ScmPathInfoStore scmPathInfoStore;

  @ObjectFactory
  ConfigurationDto createDto() {
    Links.Builder linksBuilder = linkingTo().self(self());
    if (ConfigurationPermissions.write("ssh").isPermitted()) {
      linksBuilder.single(link("update", update()));
    }
    return new ConfigurationDto(linksBuilder.build());
  }

  public abstract ConfigurationDto map(Configuration config);

  public abstract Configuration map(ConfigurationDto dto);

  private String self() {
    LinkBuilder linkBuilder = new LinkBuilder(scmPathInfoStore.get(), ConfigResource.class);
    return linkBuilder.method("getConfig").parameters().href();
  }

  private String update() {
    LinkBuilder linkBuilder = new LinkBuilder(scmPathInfoStore.get(), ConfigResource.class);
    return linkBuilder.method("setConfig").parameters().href();
  }
}
