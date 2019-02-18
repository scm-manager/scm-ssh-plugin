package com.cloudogu.scm.ssh.auth;

import com.google.inject.Guice;
import com.google.inject.Injector;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class AuthModuleTest {

  @Test
  void shouldInjectMapperInstance() {
    Injector injector = Guice.createInjector(new AuthModule());
    AuthorizedKeyMapper instance = injector.getInstance(AuthorizedKeyMapper.class);
    assertThat(instance).isNotNull();
  }

}
