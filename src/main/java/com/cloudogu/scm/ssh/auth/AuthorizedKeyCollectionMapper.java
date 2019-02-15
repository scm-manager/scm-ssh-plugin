package com.cloudogu.scm.ssh.auth;

import de.otto.edison.hal.Embedded;
import de.otto.edison.hal.HalRepresentation;
import sonia.scm.api.v2.resources.LinkBuilder;
import sonia.scm.api.v2.resources.ScmPathInfoStore;

import javax.inject.Inject;
import javax.inject.Provider;
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

  HalRepresentation map(List<AuthorizedKey> keys) {
    List<AuthorizedKeyDto> dtos = keys.stream()
      .map(mapper::map)
      .collect(Collectors.toList());

    String href = new LinkBuilder(scmPathInfoStore.get().get(), AuthorizedKeyResource.class)
      .method("findAll")
      .parameters()
      .href();

    return new HalRepresentation(linkingTo().self(href).build(), Embedded.embedded("keys", dtos));
  }
}
