package com.cloudogu.scm.ssh;

import org.apache.sshd.server.SshServer;

import javax.inject.Inject;

public class ConfigurationApplier implements SshServerConfigurator {

  private final ConfigStore configStore;

  @Inject
  public ConfigurationApplier(ConfigStore configStore) {
    this.configStore = configStore;
  }

  @Override
  public void configure(SshServer server) {
    server.setPort(configStore.getConfiguration().getPort());
  }
}
