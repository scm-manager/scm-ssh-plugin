package com.cloudogu.scm.ssh.command.git;

import com.google.inject.AbstractModule;
import sonia.scm.protocolcommand.CommandParser;
import sonia.scm.protocolcommand.RepositoryContextResolver;
import sonia.scm.protocolcommand.ScmSshProtocol;

public class GitSshModule extends AbstractModule {
  @Override
  protected void configure() {
    bind(CommandParser.class).to(GitCommandParser.class);
    bind(RepositoryContextResolver.class).to(GitRepositoryContextResolver.class);
    bind(ScmSshProtocol.class).to(GitSshProtocol.class);
  }
}
