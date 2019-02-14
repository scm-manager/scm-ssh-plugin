package com.cloudogu.scm.ssh.command.git;

import com.cloudogu.scm.ssh.command.RepositoryContext;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.transport.ReceivePack;
import org.eclipse.jgit.transport.resolver.ReceivePackFactory;

public class SshReceivePackFactory implements ReceivePackFactory<RepositoryContext> {
  @Override
  public ReceivePack create(RepositoryContext repositoryContext, Repository repository) {
    return new ReceivePack(repository);
  }
}
