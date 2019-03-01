package com.cloudogu.scm.ssh;

import org.apache.sshd.server.SshServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;

public class ConfigurationApplier implements SshServerConfigurator {

  private static final Logger LOG = LoggerFactory.getLogger(ConfigurationApplier.class);

  private final ConfigStore configStore;

  @Inject
  public ConfigurationApplier(ConfigStore configStore) {
    this.configStore = configStore;
  }

  @Override
  public void configure(SshServer server) {
    int port = configStore.getConfiguration().getPort();
    LOG.info("setting ssh server port to {}", port);
    server.setPort(port);
  }
}
