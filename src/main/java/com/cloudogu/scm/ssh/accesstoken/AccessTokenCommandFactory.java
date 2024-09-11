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

package com.cloudogu.scm.ssh.accesstoken;

import com.cloudogu.scm.ssh.simplecommand.SimpleCommand;
import com.cloudogu.scm.ssh.simplecommand.SimpleCommandFactory;
import sonia.scm.config.ScmConfiguration;
import sonia.scm.plugin.Extension;
import sonia.scm.security.AccessTokenBuilderFactory;

import jakarta.inject.Inject;
import java.util.Optional;

@Extension
public class AccessTokenCommandFactory implements SimpleCommandFactory {

  private final AccessTokenBuilderFactory accessTokenBuilderFactory;
  private final ScmConfiguration scmConfiguration;

  @Inject
  public AccessTokenCommandFactory(AccessTokenBuilderFactory accessTokenBuilderFactory, ScmConfiguration scmConfiguration) {
    this.accessTokenBuilderFactory = accessTokenBuilderFactory;
    this.scmConfiguration = scmConfiguration;
  }

  @Override
  public Optional<SimpleCommand> canHandle(String command) {
    if (command.startsWith("scm-access-token")) {
      return Optional.of(new AccessTokenCommand(accessTokenBuilderFactory, scmConfiguration));
    }
    return Optional.empty();
  }
}
