package com.cloudogu.scm.ssh;

import de.otto.edison.hal.Links;
import org.mapstruct.Mapper;
import org.mapstruct.ObjectFactory;
import sonia.scm.api.v2.resources.LinkBuilder;
import sonia.scm.api.v2.resources.ScmPathInfoStore;
import sonia.scm.config.ConfigurationPermissions;

import javax.inject.Inject;

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
