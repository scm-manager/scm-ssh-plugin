package com.cloudogu.scm.ssh.command.git;

import com.google.inject.AbstractModule;
import sonia.scm.protocolcommand.CommandParser;

public class GitSshModule extends AbstractModule {
  @Override
  protected void configure() {
    bind(CommandParser.class).to(GitCommandParser.class);
  }
}
