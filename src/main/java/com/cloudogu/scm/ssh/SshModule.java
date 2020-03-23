/*
 * MIT License
 *
 * Copyright (c) 2020-present Cloudogu GmbH and Contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
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
