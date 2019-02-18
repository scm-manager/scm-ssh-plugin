package com.cloudogu.scm.ssh.auth;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import sonia.scm.api.v2.resources.HalAppender;
import sonia.scm.api.v2.resources.HalEnricher;
import sonia.scm.api.v2.resources.HalEnricherContext;
import sonia.scm.api.v2.resources.LinkBuilder;
import sonia.scm.api.v2.resources.ScmPathInfoStore;

import javax.inject.Provider;

public abstract class AbstractAuthorizedKeyLinkEnricher implements HalEnricher {

  private final Provider<ScmPathInfoStore> scmPathInfoStore;

  protected AbstractAuthorizedKeyLinkEnricher(Provider<ScmPathInfoStore> scmPathInfoStore) {
    this.scmPathInfoStore = scmPathInfoStore;
  }

  @Override
  public final void enrich(HalEnricherContext context, HalAppender appender) {
    Subject subject = SecurityUtils.getSubject();
    String username = getUsername(context, subject);

    if (subject.isPermitted(Permissions.readAuthorizedKeys(username))) {
      appendLink(appender, username);
    }
  }

  private void appendLink(HalAppender appender, String username) {
    String href = new LinkBuilder(scmPathInfoStore.get().get(), AuthorizedKeyResource.class)
      .method("findAll")
      .parameters(username)
      .href();

    appender.appendLink("authorized_keys", href);
  }

  protected abstract String getUsername(HalEnricherContext context, Subject subject);
}
