package com.cloudogu.scm.ssh.auth;

import org.apache.shiro.subject.Subject;
import sonia.scm.api.v2.resources.Enrich;
import sonia.scm.api.v2.resources.HalEnricherContext;
import sonia.scm.api.v2.resources.Me;
import sonia.scm.api.v2.resources.ScmPathInfoStore;
import sonia.scm.plugin.Extension;

import javax.inject.Inject;
import javax.inject.Provider;

@Extension
@Enrich(Me.class)
public class AuthorizedKeyMeLinkEnricher extends AbstractAuthorizedKeyLinkEnricher {

  @Inject
  public AuthorizedKeyMeLinkEnricher(Provider<ScmPathInfoStore> scmPathInfoStore) {
    super(scmPathInfoStore);
  }

  @Override
  protected String getUsername(HalEnricherContext context, Subject subject) {
    return subject.getPrincipal().toString();
  }
}
