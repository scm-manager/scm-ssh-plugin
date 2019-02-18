package com.cloudogu.scm.ssh.auth;

import com.google.common.annotations.VisibleForTesting;
import de.otto.edison.hal.Link;
import de.otto.edison.hal.Links;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ObjectFactory;
import sonia.scm.api.v2.resources.LinkBuilder;
import sonia.scm.api.v2.resources.ScmPathInfoStore;

import javax.inject.Inject;
import javax.inject.Provider;

import static de.otto.edison.hal.Links.linkingTo;

@Mapper
public abstract class AuthorizedKeyMapper {

  @Inject
  private Provider<ScmPathInfoStore> scmPathInfoStore;

  @VisibleForTesting
  void setScmPathInfoStore(Provider<ScmPathInfoStore> scmPathInfoStore) {
    this.scmPathInfoStore = scmPathInfoStore;
  }

  @Mapping(target = "id", ignore = true)
  abstract AuthorizedKey map(AuthorizedKeyDto dto);

  @Mapping(target = "attributes", ignore = true)
  abstract AuthorizedKeyDto map(@Context String username, AuthorizedKey authorizedKey);

  @ObjectFactory
  AuthorizedKeyDto createDto(@Context String username, AuthorizedKey authorizedKey) {
    Links.Builder linksBuilder = linkingTo();
    linksBuilder.self( createSelfLink(username, authorizedKey) );
    linksBuilder.single(Link.link("delete", createDeleteLink(username, authorizedKey)));
    return new AuthorizedKeyDto(linksBuilder.build());
  }

  private String createSelfLink(String username, AuthorizedKey authorizedKey) {
    return new LinkBuilder(scmPathInfoStore.get().get(), AuthorizedKeyResource.class)
      .method("findById")
      .parameters(username, authorizedKey.getId())
      .href();
  }

  private String createDeleteLink(String username, AuthorizedKey authorizedKey) {
    return new LinkBuilder(scmPathInfoStore.get().get(), AuthorizedKeyResource.class)
      .method("deleteById")
      .parameters(username, authorizedKey.getId())
      .href();
  }

}
