package com.cloudogu.scm.ssh;

import com.cloudogu.scm.ssh.auth.ShiroPasswordAuthenticator;
import com.cloudogu.scm.ssh.auth.ShiroPublicKeyAuthenticator;
import com.cloudogu.scm.ssh.command.ScmCommandFactory;
import com.google.inject.AbstractModule;
import com.google.inject.multibindings.Multibinder;
import org.mapstruct.factory.Mappers;
import sonia.scm.plugin.Extension;

@Extension
public class SshModule extends AbstractModule {
  @Override
  protected void configure() {
    Multibinder<SshServerConfigurator> configurators = Multibinder.newSetBinder(binder(), SshServerConfigurator.class);
    configurators.addBinding().to(ConfigurationApplier.class);
    configurators.addBinding().to(ShiroPasswordAuthenticator.class);
    configurators.addBinding().to(ShiroPublicKeyAuthenticator.class);
    configurators.addBinding().to(KeyPairConfigurator.class);
    configurators.addBinding().to(ExecutorFactory.class);
    configurators.addBinding().to(ScmCommandFactory.class);

    bind(ConfigurationMapper.class).to(Mappers.getMapper(ConfigurationMapper.class).getClass());
  }
}
