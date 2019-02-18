package com.cloudogu.scm.ssh.auth;

import org.apache.shiro.subject.Subject;
import sonia.scm.api.v2.resources.Enrich;
import sonia.scm.api.v2.resources.HalEnricherContext;
import sonia.scm.api.v2.resources.ScmPathInfoStore;
import sonia.scm.plugin.Extension;
import sonia.scm.user.User;

import javax.inject.Inject;
import javax.inject.Provider;

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
