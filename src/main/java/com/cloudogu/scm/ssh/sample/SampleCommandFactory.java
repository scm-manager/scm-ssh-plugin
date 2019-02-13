package com.cloudogu.scm.ssh.sample;

import org.apache.sshd.server.command.Command;
import org.apache.sshd.server.command.CommandFactory;
import sonia.scm.repository.RepositoryManager;

import javax.inject.Inject;

public class SampleCommandFactory implements CommandFactory {

  private final RepositoryManager repositoryManager;

  @Inject
  public SampleCommandFactory(RepositoryManager repositoryManager) {
    this.repositoryManager = repositoryManager;
  }

  @Override
  public Command createCommand(String command) {
    if ("list-repositories".equals(command)) {
      return new ListRepositoriesCommand(repositoryManager);
    }
    return new UnknownCommand(command);
  }

}
