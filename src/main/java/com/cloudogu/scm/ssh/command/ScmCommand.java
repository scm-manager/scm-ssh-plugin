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
package com.cloudogu.scm.ssh.command;

import com.cloudogu.scm.ssh.Attributes;
import com.cloudogu.scm.ssh.simplecommand.SimpleCommand;
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
    int exitCode = simpleCommand.execute(createCommandContext(command, new String[0]));
    LOG.debug("finished execution of command '{}'", command);
    return exitCode;
  }

  private void executeProtocolCommand(CommandInterpreter commandInterpreter, String command) throws IOException {
    String[] args = commandInterpreter.getParsedArgs();
    RepositoryContext repositoryContext = commandInterpreter.getRepositoryContextResolver().resolve(args);
    CommandContext commandContext = createCommandContext(command, args);
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

  private CommandContext createCommandContext(String command, String[] args) {
    return new CommandContext(command, args, in, out, err);
  }
}
