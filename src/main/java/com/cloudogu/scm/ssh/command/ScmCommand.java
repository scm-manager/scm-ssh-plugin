package com.cloudogu.scm.ssh.command;

import com.cloudogu.scm.ssh.Attributes;
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

import java.io.OutputStream;
import java.util.Optional;
import java.util.Set;

public class ScmCommand extends AbstractCommandSupport {

  private static final Logger LOG = LoggerFactory.getLogger(ScmCommand.class);

  private final Set<CommandInterpreterFactory> commandInterpreterFactories;

  ScmCommand(String command, CloseableExecutorService executorService, Set<CommandInterpreterFactory> commandInterpreterFactories) {
    super(command, executorService);
    this.commandInterpreterFactories = commandInterpreterFactories;
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
      ThreadContext.bind(getSession().getAttribute(Attributes.SUBJECT));

      CommandInterpreter commandInterpreter = commandInterpreterFactories.stream()
        .map(p -> p.canHandle(command))
        .filter(Optional::isPresent)
        .map(Optional::get)
        .findFirst()
        .orElseThrow(() -> new IllegalArgumentException("no interpreter found for command: " + command));

      String[] args = commandInterpreter.getParsedArgs();

      RepositoryContext repositoryContext = commandInterpreter.getRepositoryContextResolver().resolve(args);
      CommandContext commandContext = createCommandContext(command, args);

      commandInterpreter.getProtocolHandler().handle(commandContext, repositoryContext);

      LOG.debug("finished protocol handling of command '{}'", command);

      onExit(0);
    } catch (Exception throwable) {
      LOG.error("received error during handling of command {}", command, throwable);

      onExit(-1, throwable.getClass().getSimpleName());
    } finally {
      ThreadContext.unbindSubject();
    }
  }

  private CommandContext createCommandContext(String command, String[] args) {
    return new CommandContext(command, args, in, out, err);
  }


}
