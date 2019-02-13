package com.cloudogu.scm.ssh.sample;

import org.apache.sshd.server.Environment;

import java.io.IOException;

class UnknownCommand extends CharBasedCommand {

  private final String command;

  UnknownCommand(String command) {
    this.command = command;
  }

  @Override
  public void start(Environment env) throws IOException {
    String user = env.getEnv().get("USER");

    writer.write("hello ");
    writer.write(user);
    writer.newLine();

    writer.write("i'm sorry, but i don't know the command \"");
    writer.write(command);
    writer.write("\"");
    writer.newLine();

    writer.newLine();

    writer.write("try list-repositories for example");
    writer.newLine();

    writer.flush();
    callback.onExit(0, "bye " + user);
  }

}
