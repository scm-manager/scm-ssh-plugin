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

import com.cloudogu.scm.ssh.Attributes;
import com.cloudogu.scm.ssh.simplecommand.SimpleCommand;
import com.cloudogu.scm.ssh.simplecommand.SimpleCommandContext;
import com.cloudogu.scm.ssh.simplecommand.SimpleCommandFactory;
import org.apache.shiro.util.ThreadContext;
import org.apache.sshd.common.channel.ChannelOutputStream;
import org.apache.sshd.common.util.threads.CloseableExecutorService;
import org.apache.sshd.server.command.AbstractCommandSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sonia.scm.protocolcommand.CommandContext;
import sonia.scm.protocolcommand.CommandInterpreter;
import sonia.scm.protocolcommand.CommandInterpreterFactory;
import sonia.scm.protocolcommand.RepositoryContext;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Optional;
import java.util.Set;

public class ScmCommand extends AbstractCommandSupport {

  private static final Logger LOG = LoggerFactory.getLogger(ScmCommand.class);

  private final Set<CommandInterpreterFactory> commandInterpreterFactories;
  private final Set<SimpleCommandFactory> simpleCommandFactories;

  ScmCommand(
    String command,
    CloseableExecutorService executorService,
    Set<CommandInterpreterFactory> commandInterpreterFactories,
    Set<SimpleCommandFactory> simpleCommandFactories
  ) {
    super(command, executorService);
    this.commandInterpreterFactories = commandInterpreterFactories;
    this.simpleCommandFactories = simpleCommandFactories;
  }

  @Override
  public void setOutputStream(OutputStream out) {
    super.setOutputStream(out);
    if (out instanceof ChannelOutputStream) {
      ((ChannelOutputStream) out).setNoDelay(true);
    }
  }

  @Override
  public void setErrorStream(OutputStream err) {
    super.setErrorStream(err);
    if (err instanceof ChannelOutputStream) {
      ((ChannelOutputStream) err).setNoDelay(true);
    }
  }

  @Override
  public void run() {
    String command = getCommand();
    LOG.debug("trying to run command '{}'", command);
    try {
      int exitCode = 0;
      ThreadContext.bind(getSession().getAttribute(Attributes.SUBJECT));

      Optional<SimpleCommand> optionalSimpleCommand = getSimpleCommand(command);
      if (optionalSimpleCommand.isPresent()) {
        exitCode = executeSimpleCommand(optionalSimpleCommand.get(), command);
      }

      Optional<CommandInterpreter> optionalCommandInterpreter = getCommandInterpreter(command);
      if (optionalCommandInterpreter.isPresent()) {
        executeProtocolCommand(optionalCommandInterpreter.get(), command);
      }

      if (!optionalSimpleCommand.isPresent() && !optionalCommandInterpreter.isPresent()) {
        throw new IllegalArgumentException("no interpreter found for command: " + command);
      }

      onExit(exitCode);
    } catch (Exception throwable) {
      LOG.error("received error during handling of command {}", command, throwable);

      onExit(-1, throwable.getClass().getSimpleName());
    } finally {
      ThreadContext.unbindSubject();
    }
  }

  private int executeSimpleCommand(SimpleCommand simpleCommand, String command) throws IOException {
    SimpleCommandContext context = new SimpleCommandContext(
      command, getInputStream(), getOutputStream(), getErrorStream()
    );
    int exitCode = simpleCommand.execute(context);
    LOG.debug("finished execution of command '{}'", command);
    return exitCode;
  }

  private void executeProtocolCommand(CommandInterpreter commandInterpreter, String command) throws IOException {
    String[] args = commandInterpreter.getParsedArgs();
    RepositoryContext repositoryContext = commandInterpreter.getRepositoryContextResolver().resolve(args);
    CommandContext commandContext = new CommandContext(
      command, args, getInputStream(), getOutputStream(), getErrorStream()
    );
    commandInterpreter.getProtocolHandler().handle(commandContext, repositoryContext);
    LOG.debug("finished protocol handling of command '{}'", command);
  }

  private Optional<SimpleCommand> getSimpleCommand(String command) {
    return simpleCommandFactories
      .stream()
      .map(p -> p.canHandle(command))
      .filter(Optional::isPresent)
      .map(Optional::get)
      .findFirst();
  }

  private Optional<CommandInterpreter> getCommandInterpreter(String command) {
    return commandInterpreterFactories
      .stream()
      .map(p -> p.canHandle(command))
      .filter(Optional::isPresent)
      .map(Optional::get)
      .findFirst();
  }
}
