package com.cloudogu.scm.ssh.auth;

import com.google.inject.AbstractModule;
import org.mapstruct.factory.Mappers;
import sonia.scm.plugin.Extension;

@Extension
public class AuthModule extends AbstractModule {

  @Override
  protected void configure() {
    bind(AuthorizedKeyMapper.class).to(Mappers.getMapper(AuthorizedKeyMapper.class).getClass());
  }
}
