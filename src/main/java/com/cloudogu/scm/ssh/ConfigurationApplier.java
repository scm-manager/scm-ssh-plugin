package com.cloudogu.scm.ssh;

import org.apache.sshd.server.SshServer;

import javax.inject.Inject;

public class ConfigurationApplier implements SshServerConfigurator {

  private final Configuration configuration;

  @Inject
  public ConfigurationApplier(Configuration configuration) {
    this.configuration = configuration;
  }

  @Override
  public void configure(SshServer server) {
    server.setPort(configuration.getPort());
  }
}
