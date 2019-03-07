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

  private final Set<CommandInterpreterFactory> commandInterpreterFactories;

  @Inject
  public ScmCommandFactory(Set<CommandInterpreterFactory> commandInterpreterFactories) {
    super(ScmCommandFactory.class.getSimpleName());
    this.commandInterpreterFactories = commandInterpreterFactories;
  }

  @Override
  public boolean isSupportedCommand(String command) {
    return true;
  }

  @Override
  protected ScmCommand executeSupportedCommand(String command) {
    LOG.debug("create scm command for '{}'", command);
    return new ScmCommand(command, Executors.get(), commandInterpreterFactories);
  }

  @Override
  public void configure(SshServer server) {
    server.setCommandFactory(this);
  }
}
