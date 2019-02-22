package com.cloudogu.scm.ssh;

import org.apache.sshd.server.SshServer;
import org.junit.jupiter.api.Test;
import sonia.scm.store.InMemoryConfigurationStoreFactory;

import static org.assertj.core.api.Assertions.assertThat;

class ConfigurationApplierTest {

  @Test
  void shouldApplyConfiguration() {
    Configuration configuration = new Configuration();
    configuration.setPort(42);

    ConfigStore configStore = new ConfigStore(new InMemoryConfigurationStoreFactory(), null);
    configStore.setConfiguration(configuration);

    ConfigurationApplier applier = new ConfigurationApplier(configStore);

    SshServer server = new SshServer();
    applier.configure(server);

    assertThat(server.getPort()).isEqualTo(42);
  }

}
