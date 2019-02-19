package com.cloudogu.scm.ssh;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sonia.scm.config.ScmConfiguration;
import sonia.scm.repository.Repository;
import sonia.scm.repository.RepositoryTestData;
import sonia.scm.repository.api.ScmProtocol;

import static org.assertj.core.api.Assertions.assertThat;

class SshProtocolProviderTest {

  private Configuration sshConfiguration;
  private ScmConfiguration scmConfiguration;

  private SshProtocolProvider protocolProvider;

  @BeforeEach
  void setUpObjectUnderTest() {
    sshConfiguration = new Configuration();
    scmConfiguration = new ScmConfiguration();
    protocolProvider = new SshProtocolProvider(sshConfiguration, scmConfiguration);
  }

  @Test
  void shouldReturnSshProtocol() {
    sshConfiguration.setPort(2022);
    scmConfiguration.setBaseUrl("https://scm.hitchhiker.com/scm");

    Repository heartOfGold = RepositoryTestData.createHeartOfGold("git");
    ScmProtocol sshProtocol = protocolProvider.get(heartOfGold);

    assertThat(sshProtocol.getType()).isEqualTo("git");
    assertThat(sshProtocol.getUrl()).isEqualTo("ssh://scm.hitchhiker.com:2022/repo/hitchhiker/HeartOfGold");
  }

}
