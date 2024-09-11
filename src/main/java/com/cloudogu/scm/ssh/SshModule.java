/*
 * Copyright (c) 2020 - present Cloudogu GmbH
 *
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, version 3.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see https://www.gnu.org/licenses/.
 */

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
