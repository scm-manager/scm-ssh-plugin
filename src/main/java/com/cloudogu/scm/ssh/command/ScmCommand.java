package com.cloudogu.scm.ssh.command;

import com.cloudogu.scm.ssh.Attributes;
import org.apache.shiro.util.ThreadContext;
import org.apache.sshd.common.channel.ChannelOutputStream;
import org.apache.sshd.common.util.threads.CloseableExecutorService;
import org.apache.sshd.server.command.AbstractCommandSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sonia.scm.protocolcommand.CommandContext;
import sonia.scm.protocolcommand.CommandParser;
import sonia.scm.protocolcommand.RepositoryContext;
import sonia.scm.protocolcommand.RepositoryContextResolver;
import sonia.scm.protocolcommand.ScmSshProtocol;

import java.io.OutputStream;

public class ScmCommand extends AbstractCommandSupport {

  private static final Logger LOG = LoggerFactory.getLogger(ScmCommand.class);

  private RepositoryContextResolver contextResolver;
  private CommandParser commandParser;
  private ScmSshProtocol protocol;

  ScmCommand(String command, CloseableExecutorService executorService, CommandParser commandParser, RepositoryContextResolver contextResolver, ScmSshProtocol protocol) {
    super(command, executorService);
    this.commandParser = commandParser;
    this.contextResolver = contextResolver;
    this.protocol = protocol;
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

      String[] args = commandParser.parse(command);

      RepositoryContext repositoryContext = contextResolver.resolve(args);
      CommandContext commandContext = createCommandContext(command, args);

      protocol.handle(commandContext, repositoryContext);

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
