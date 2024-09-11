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

package com.cloudogu.scm.ssh.command;

import com.cloudogu.scm.ssh.SshServerConfigurator;
import com.cloudogu.scm.ssh.simplecommand.SimpleCommandFactory;
import org.apache.sshd.server.SshServer;
import org.apache.sshd.server.channel.ChannelSession;
import org.apache.sshd.server.command.AbstractDelegatingCommandFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sonia.scm.protocolcommand.CommandInterpreterFactory;

import jakarta.inject.Inject;
import java.util.Set;

public class ScmCommandFactory extends AbstractDelegatingCommandFactory implements SshServerConfigurator {

  private static final Logger LOG = LoggerFactory.getLogger(ScmCommandFactory.class);

  private final Set<CommandInterpreterFactory> commandInterpreterFactories;
  private final Set<SimpleCommandFactory> simpleCommandFactories;

  @Inject
  public ScmCommandFactory(Set<CommandInterpreterFactory> commandInterpreterFactories, Set<SimpleCommandFactory> simpleCommandFactories) {
    super(ScmCommandFactory.class.getSimpleName());
    this.commandInterpreterFactories = commandInterpreterFactories;
    this.simpleCommandFactories = simpleCommandFactories;
  }

  @Override
  public boolean isSupportedCommand(ChannelSession channel, String command) {
    return true;
  }

  @Override
  protected ScmCommand executeSupportedCommand(ChannelSession channel, String command) {
    LOG.debug("create scm command for '{}'", command);
    return new ScmCommand(command, Executors.get(), commandInterpreterFactories, simpleCommandFactories);
  }

  @Override
  public void configure(SshServer server) {
    server.setCommandFactory(this);
  }
}
