package com.cloudogu.scm.ssh.command;

import com.cloudogu.scm.ssh.SshServerConfigurator;
import org.apache.sshd.server.SshServer;
import org.apache.sshd.server.command.AbstractDelegatingCommandFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sonia.scm.protocolcommand.CommandParser;
import sonia.scm.protocolcommand.RepositoryContextResolver;
import sonia.scm.protocolcommand.ScmCommandProtocol;

import javax.inject.Inject;
import java.util.Set;

public class ScmCommandFactory extends AbstractDelegatingCommandFactory implements SshServerConfigurator {

  private static final Logger LOG = LoggerFactory.getLogger(ScmCommandFactory.class);

  private final CommandParser commandParser;
  private final RepositoryContextResolver repositoryContextResolver;
  private final Set<ScmCommandProtocol> protocols;

  @Inject
  public ScmCommandFactory(CommandParser commandParser, RepositoryContextResolver repositoryContextResolver, Set<ScmCommandProtocol> protocols) {
    super(ScmCommandFactory.class.getSimpleName());
    this.commandParser = commandParser;
    this.repositoryContextResolver = repositoryContextResolver;
    this.protocols = protocols;
  }

  @Override
  public boolean isSupportedCommand(String command) {
    return true;
  }

  @Override
  protected ScmCommand executeSupportedCommand(String command) {
    LOG.debug("create scm command for '{}'", command);
    // TODO
    // we create a new executor for every command, this sounds not right
    // but with a cached thread pool the server hangs on the second request
    return new ScmCommand(command, Executors.create(), commandParser, repositoryContextResolver, protocols);
  }

  @Override
  public void configure(SshServer server) {
    server.setCommandFactory(this);
  }
}
