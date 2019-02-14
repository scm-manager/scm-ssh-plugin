package com.cloudogu.scm.ssh.command;

@FunctionalInterface
public interface CommandParser {

  String[] parse(String command);

}
