package com.cloudogu.scm.ssh.command.git;

import sonia.scm.plugin.Extension;
import sonia.scm.protocolcommand.CommandInterpreter;
import sonia.scm.protocolcommand.CommandInterpreterFactory;
import sonia.scm.protocolcommand.git.GitCommandProtocol;

import javax.inject.Inject;
import java.util.Optional;

import static java.util.Optional.empty;
import static java.util.Optional.of;

@Extension
public class GitCommandInterpreterFactory implements CommandInterpreterFactory {
  private final GitCommandProtocol gitCommandProtocol;

  @Inject
  public GitCommandInterpreterFactory(GitCommandProtocol gitCommandProtocol) {
    this.gitCommandProtocol = gitCommandProtocol;
  }

  @Override
  public Optional<CommandInterpreter> canHandle(String command) {
    try {
      String[] args = new GitCommandParser().parse(command);
      if (args[0].startsWith("git")) {
        return of(new GitCommandInterpreter(gitCommandProtocol, args));
      } else {
        return empty();
      }
    } catch (IllegalArgumentException e) {
      return empty();
    }
  }
}
