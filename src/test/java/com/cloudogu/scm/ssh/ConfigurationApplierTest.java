package com.cloudogu.scm.ssh;

import org.apache.sshd.server.SshServer;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ConfigurationApplierTest {

  @Test
  void shouldApplyConfiguration() {
    Configuration configuration = new Configuration();
    configuration.setPort(42);

    ConfigurationApplier applier = new ConfigurationApplier(configuration);

    SshServer server = new SshServer();
    applier.configure(server);

    assertThat(server.getPort()).isEqualTo(42);
  }

}
