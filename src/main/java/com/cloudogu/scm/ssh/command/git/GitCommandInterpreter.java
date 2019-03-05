package com.cloudogu.scm.ssh.command.git;

import sonia.scm.plugin.Extension;
import sonia.scm.protocolcommand.CommandInterpreter;
import sonia.scm.protocolcommand.CommandParser;
import sonia.scm.protocolcommand.ScmCommandProtocol;
import sonia.scm.protocolcommand.git.GitCommandProtocol;

import javax.inject.Inject;

@Extension
public class GitCommandInterpreter implements CommandInterpreter {
  private final GitCommandProtocol gitCommandProtocol;

  @Inject
  public GitCommandInterpreter(GitCommandProtocol gitCommandProtocol) {
    this.gitCommandProtocol = gitCommandProtocol;
  }

  @Override
  public boolean canHandle(String command) {
    try {
      String[] args = new GitCommandParser().parse(command);
      return args[0].startsWith("git");
    } catch (IllegalArgumentException e) {
      return false;
    }
  }

  @Override
  public CommandParser getParser() {
    return new GitCommandParser();
  }

  @Override
  public ScmCommandProtocol getProtocolHandler() {
    return gitCommandProtocol;
  }
}
