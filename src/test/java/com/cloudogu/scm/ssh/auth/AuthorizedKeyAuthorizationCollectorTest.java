package com.cloudogu.scm.ssh.auth;

import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.subject.SimplePrincipalCollection;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class AuthorizedKeyAuthorizationCollectorTest {

  private final AuthorizedKeyAuthorizationCollector collector = new AuthorizedKeyAuthorizationCollector();

  @Test
  void shouldReturnDefaultPermissions() {
    SimplePrincipalCollection collection = new SimplePrincipalCollection("trillian", "test");

    AuthorizationInfo info = collector.collect(collection);

    assertThat(info.getStringPermissions()).containsOnly("user:readAuthorizedKeys,writeAuthorizedKeys:trillian");
  }

}
