package com.cloudogu.scm.ssh;

import org.apache.shiro.subject.Subject;
import org.apache.shiro.util.ThreadContext;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import sonia.scm.config.ScmConfiguration;
import sonia.scm.repository.Repository;
import sonia.scm.repository.RepositoryTestData;
import sonia.scm.repository.api.ScmProtocol;
import sonia.scm.store.InMemoryConfigurationStoreFactory;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SshProtocolProviderTest {

  @Mock
  private Subject subject;

  private ConfigStore sshConfigStore;
  private SshProtocolProvider protocolProvider;
  private ScmConfiguration scmConfiguration;

  @BeforeEach
  void setUpObjectUnderTest() {
    ThreadContext.bind(subject);

    scmConfiguration = new ScmConfiguration();
    sshConfigStore = new ConfigStore(new InMemoryConfigurationStoreFactory(), scmConfiguration);
    protocolProvider = new SshProtocolProvider(sshConfigStore);
  }

  @AfterEach
  void clearThreadContext() {
    ThreadContext.unbindSubject();
  }

  @Test
  void shouldReturnSshProtocol() {
    ScmProtocol sshProtocol = configureAndGet(2022);

    assertThat(sshProtocol.getType()).isEqualTo("ssh");
    assertThat(sshProtocol.getUrl()).isEqualTo("ssh://scm.hitchhiker.com:2022/repo/hitchhiker/HeartOfGold");
  }

  private ScmProtocol configureAndGet(int port) {
    Configuration sshConfiguration = new Configuration();
    sshConfiguration.setPort(port);
    sshConfigStore.setConfiguration(sshConfiguration);

    scmConfiguration.setBaseUrl("https://scm.hitchhiker.com/scm");

    Repository heartOfGold = RepositoryTestData.createHeartOfGold("git");
    return protocolProvider.get(heartOfGold);
  }

  @Test
  void shouldReturnSshProtocolWithUsername() {
    when(subject.isAuthenticated()).thenReturn(true);
    when(subject.getPrincipal()).thenReturn("trillian");

    ScmProtocol sshProtocol = configureAndGet(2022);

    assertThat(sshProtocol.getType()).isEqualTo("ssh");
    assertThat(sshProtocol.getUrl()).isEqualTo("ssh://trillian@scm.hitchhiker.com:2022/repo/hitchhiker/HeartOfGold");
  }

  @Test
  void shouldReturnSshProtocolWithoutPort() {
    ScmProtocol sshProtocol = configureAndGet(22);

    assertThat(sshProtocol.getType()).isEqualTo("ssh");
    assertThat(sshProtocol.getUrl()).isEqualTo("ssh://scm.hitchhiker.com/repo/hitchhiker/HeartOfGold");
  }

}
