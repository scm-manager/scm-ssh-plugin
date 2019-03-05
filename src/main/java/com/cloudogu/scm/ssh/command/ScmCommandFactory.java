package com.cloudogu.scm.ssh.command;

import com.cloudogu.scm.ssh.SshServerConfigurator;
import org.apache.sshd.server.SshServer;
import org.apache.sshd.server.command.AbstractDelegatingCommandFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sonia.scm.protocolcommand.CommandInterpreterFactory;
import sonia.scm.protocolcommand.RepositoryContextResolver;

import javax.inject.Inject;
import java.util.Set;

public class ScmCommandFactory extends AbstractDelegatingCommandFactory implements SshServerConfigurator {

  private static final Logger LOG = LoggerFactory.getLogger(ScmCommandFactory.class);

  private final RepositoryContextResolver repositoryContextResolver;
  private final Set<CommandInterpreterFactory> commandInterpreterFactories;

  @Inject
  public ScmCommandFactory(RepositoryContextResolver repositoryContextResolver, Set<CommandInterpreterFactory> commandInterpreterFactories) {
    super(ScmCommandFactory.class.getSimpleName());
    this.repositoryContextResolver = repositoryContextResolver;
    this.commandInterpreterFactories = commandInterpreterFactories;
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
    return new ScmCommand(command, Executors.create(), commandInterpreterFactories, repositoryContextResolver);
  }

  @Override
  public void configure(SshServer server) {
    server.setCommandFactory(this);
  }
}
