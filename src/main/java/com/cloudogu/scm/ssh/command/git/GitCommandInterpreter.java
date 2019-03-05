package com.cloudogu.scm.ssh.command.git;

import sonia.scm.protocolcommand.CommandInterpreter;
import sonia.scm.protocolcommand.ScmCommandProtocol;
import sonia.scm.protocolcommand.git.GitCommandProtocol;

class GitCommandInterpreter implements CommandInterpreter {
  private final GitCommandProtocol gitCommandProtocol;
  private final String[] args;

  GitCommandInterpreter(GitCommandProtocol gitCommandProtocol, String[] args) {
    this.gitCommandProtocol = gitCommandProtocol;
    this.args = args;
  }

  @Override
  public String[] getParsedArgs() {
    return args;
  }

  @Override
  public ScmCommandProtocol getProtocolHandler() {
    return gitCommandProtocol;
  }
}
