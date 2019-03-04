package com.cloudogu.scm.ssh.command.git;

import org.apache.sshd.git.AbstractGitCommand;
import sonia.scm.protocolcommand.CommandParser;

import java.util.List;

public class GitCommandParser implements CommandParser {
  @Override
  public String[] parse(String command) {
    List<String> strs = AbstractGitCommand.parseDelimitedString(command, " ", true);
    String[] args = strs.toArray(new String[strs.size()]);
    for (int i = 0; i < args.length; i++) {
      String argVal = args[i];
      if (argVal.startsWith("'") && argVal.endsWith("'")) {
        args[i] = argVal.substring(1, argVal.length() - 1);
        argVal = args[i];
      }
      if (argVal.startsWith("\"") && argVal.endsWith("\"")) {
        args[i] = argVal.substring(1, argVal.length() - 1);
      }
    }

    if (args.length != 2) {
      throw new IllegalArgumentException("Invalid git command line (no arguments): " + command);
    }
    return args;
  }
}
