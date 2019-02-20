package com.cloudogu.scm.ssh.auth;

import de.otto.edison.hal.Embedded;
import de.otto.edison.hal.HalRepresentation;
import de.otto.edison.hal.Link;
import de.otto.edison.hal.Links;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
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
    Subject subject = SecurityUtils.getSubject();
    return subject.isPermitted(Permissions.writeAuthorizedKeys(username));
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
