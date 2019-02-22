package com.cloudogu.scm.ssh;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sonia.scm.config.ScmConfiguration;
import sonia.scm.repository.Repository;
import sonia.scm.repository.RepositoryTestData;
import sonia.scm.repository.api.ScmProtocol;
import sonia.scm.store.InMemoryConfigurationStoreFactory;

import static org.assertj.core.api.Assertions.assertThat;

class SshProtocolProviderTest {

  private ConfigStore sshConfigStore;

  private SshProtocolProvider protocolProvider;
  private ScmConfiguration scmConfiguration;

  @BeforeEach
  void setUpObjectUnderTest() {
    scmConfiguration = new ScmConfiguration();
    sshConfigStore = new ConfigStore(new InMemoryConfigurationStoreFactory(), scmConfiguration);
    protocolProvider = new SshProtocolProvider(sshConfigStore);
  }

  @Test
  void shouldReturnSshProtocol() {
    Configuration sshConfiguration = new Configuration();
    sshConfiguration.setPort(2022);
    sshConfigStore.setConfiguration(sshConfiguration);

    scmConfiguration.setBaseUrl("https://scm.hitchhiker.com/scm");

    Repository heartOfGold = RepositoryTestData.createHeartOfGold("git");
    ScmProtocol sshProtocol = protocolProvider.get(heartOfGold);

    assertThat(sshProtocol.getType()).isEqualTo("ssh");
    assertThat(sshProtocol.getUrl()).isEqualTo("ssh://scm.hitchhiker.com:2022/repo/hitchhiker/HeartOfGold");
  }

}
