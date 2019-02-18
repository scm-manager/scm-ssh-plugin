package com.cloudogu.scm.ssh.auth;

import sonia.scm.api.v2.resources.Enrich;
import sonia.scm.api.v2.resources.HalAppender;
import sonia.scm.api.v2.resources.HalEnricher;
import sonia.scm.api.v2.resources.HalEnricherContext;
import sonia.scm.api.v2.resources.LinkBuilder;
import sonia.scm.api.v2.resources.ScmPathInfoStore;
import sonia.scm.plugin.Extension;
import sonia.scm.user.User;

import javax.inject.Inject;
import javax.inject.Provider;

@Extension
@Enrich(User.class)
public class AuthorizedKeyUserLinkEnricher implements HalEnricher {

  private final Provider<ScmPathInfoStore> scmPathInfoStore;

  @Inject
  public AuthorizedKeyUserLinkEnricher(Provider<ScmPathInfoStore> scmPathInfoStore) {
    this.scmPathInfoStore = scmPathInfoStore;
  }

  @Override
  public void enrich(HalEnricherContext context, HalAppender appender) {
    User user = context.oneRequireByType(User.class);
    String href = new LinkBuilder(scmPathInfoStore.get().get(), AuthorizedKeyResource.class)
      .method("findAll")
      .parameters(user.getName())
      .href();

    appender.appendLink("authorized_keys", href);
  }
}
