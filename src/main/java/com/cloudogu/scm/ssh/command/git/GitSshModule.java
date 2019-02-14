package com.cloudogu.scm.ssh.command.git;

import com.cloudogu.scm.ssh.command.CommandParser;
import com.cloudogu.scm.ssh.command.RepositoryContextResolver;
import com.cloudogu.scm.ssh.command.ScmSshProtocol;
import com.google.inject.AbstractModule;

public class GitSshModule extends AbstractModule {
  @Override
  protected void configure() {
    bind(CommandParser.class).to(GitCommandParser.class);
    bind(RepositoryContextResolver.class).to(GitRepositoryContextResolver.class);
    bind(ScmSshProtocol.class).to(GitSshProtocol.class);
  }
}
