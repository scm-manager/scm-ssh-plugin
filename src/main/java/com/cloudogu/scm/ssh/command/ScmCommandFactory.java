package com.cloudogu.scm.ssh.command;

import com.cloudogu.scm.ssh.SshServerConfigurator;
import org.apache.sshd.server.SshServer;
import org.apache.sshd.server.command.AbstractDelegatingCommandFactory;

import javax.inject.Inject;

public class ScmCommandFactory extends AbstractDelegatingCommandFactory implements SshServerConfigurator {

  private CommandParser commandParser;
  private RepositoryContextResolver repositoryContextResolver;
  private ScmSshProtocol protocol;

  @Inject
  public ScmCommandFactory(CommandParser commandParser, RepositoryContextResolver repositoryContextResolver, ScmSshProtocol protocol) {
    super(ScmCommandFactory.class.getSimpleName());
    this.commandParser = commandParser;
    this.repositoryContextResolver = repositoryContextResolver;
    this.protocol = protocol;
  }

  @Override
  public boolean isSupportedCommand(String command) {
    return true;
  }

  @Override
  protected ScmCommand executeSupportedCommand(String command) {
    // TODO
    // we create a new executor for every command, this sounds not right
    // but with a cached thread pool the server hangs on the second request
    return new ScmCommand(command, Executors.create(), commandParser, repositoryContextResolver, protocol);
  }

  @Override
  public void configure(SshServer server) {
    server.setCommandFactory(this);
  }
}
