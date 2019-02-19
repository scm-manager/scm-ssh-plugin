package com.cloudogu.scm.ssh;

import org.apache.sshd.server.SshServer;
import org.apache.sshd.server.keyprovider.SimpleGeneratorHostKeyProvider;
import sonia.scm.SCMContextProvider;

import javax.inject.Inject;
import java.nio.file.Path;
import java.nio.file.Paths;

public class KeyPairConfigurator implements SshServerConfigurator {

  private SCMContextProvider context;

  @Inject
  public KeyPairConfigurator(SCMContextProvider context) {
    this.context = context;
  }

  @Override
  public void configure(SshServer server) {
    Path path = context.resolve(Paths.get("config", "ssh-hostkeys.ser"));
    server.setKeyPairProvider(new SimpleGeneratorHostKeyProvider(path));
  }
}
