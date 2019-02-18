package com.cloudogu.scm.ssh.auth;

import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.subject.PrincipalCollection;
import sonia.scm.plugin.Extension;
import sonia.scm.security.AuthorizationCollector;

@Extension
public class AuthorizedKeyAuthorizationCollector implements AuthorizationCollector {

  @Override
  public AuthorizationInfo collect(PrincipalCollection principalCollection) {
    SimpleAuthorizationInfo authorizationInfo = new SimpleAuthorizationInfo();

    String username = principalCollection.getPrimaryPrincipal().toString();
    authorizationInfo.addStringPermission(Permissions.readAndWriteAuthorizedKeys(username));

    return authorizationInfo;
  }

}
