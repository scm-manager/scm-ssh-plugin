package com.cloudogu.scm.ssh.sample;

import org.apache.sshd.server.Environment;
import sonia.scm.repository.Repository;
import sonia.scm.repository.RepositoryManager;

import java.io.IOException;

public class ListRepositoriesCommand extends CharBasedCommand {
  private final RepositoryManager repositoryManager;

  ListRepositoriesCommand(RepositoryManager repositoryManager) {
    this.repositoryManager = repositoryManager;
  }

  @Override
  public void start(Environment env) throws IOException {
    writer.write("repositories:");
    writer.newLine();

    for (Repository repository : repositoryManager.getAll()) {
      writer.write(" - ");
      writer.write(repository.getNamespace());
      writer.write("/");
      writer.write(repository.getName());
      writer.newLine();
    }

    writer.flush();
    callback.onExit(0);
  }
}
