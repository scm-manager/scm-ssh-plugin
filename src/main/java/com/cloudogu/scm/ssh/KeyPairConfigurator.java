package com.cloudogu.scm.ssh;

import org.apache.sshd.server.SshServer;
import org.apache.sshd.server.keyprovider.SimpleGeneratorHostKeyProvider;

import java.nio.file.Paths;

public class KeyPairConfigurator implements SshServerConfigurator {
  @Override
  public void configure(SshServer server) {
    server.setKeyPairProvider(new SimpleGeneratorHostKeyProvider(Paths.get("hostkey.ser")));
  }
}
