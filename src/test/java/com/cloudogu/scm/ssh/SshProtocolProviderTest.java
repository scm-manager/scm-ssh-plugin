/*
 * MIT License
 *
 * Copyright (c) 2020-present Cloudogu GmbH and Contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
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
    return configureAndGet(sshConfiguration);
  }

  private ScmProtocol configureAndGet(Configuration sshConfiguration) {
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

  @Test
  void shouldKeepConfiguredPort() {
    Configuration sshConfiguration = new Configuration();
    sshConfiguration.setHostName("ssh.hitchhiker.com:2200");
    sshConfiguration.setPort(2222);

    ScmProtocol sshProtocol = configureAndGet(sshConfiguration);
    assertThat(sshProtocol.getUrl()).isEqualTo("ssh://ssh.hitchhiker.com:2200/repo/hitchhiker/HeartOfGold");
  }

  @Test
  void shouldKeepConfiguredPortButShouldNotDisplay22() {
    Configuration sshConfiguration = new Configuration();
    sshConfiguration.setHostName("ssh.hitchhiker.com:22");
    sshConfiguration.setPort(2222);

    ScmProtocol sshProtocol = configureAndGet(sshConfiguration);
    assertThat(sshProtocol.getUrl()).isEqualTo("ssh://ssh.hitchhiker.com/repo/hitchhiker/HeartOfGold");
  }

  @Test
  void shouldIgnoreConfiguredSshProtocolPrefix() {
    Configuration sshConfiguration = new Configuration();
    sshConfiguration.setHostName("ssh://ssh.hitchhiker.com:22");
    sshConfiguration.setPort(2222);

    ScmProtocol sshProtocol = configureAndGet(sshConfiguration);
    assertThat(sshProtocol.getUrl()).isEqualTo("ssh://ssh.hitchhiker.com/repo/hitchhiker/HeartOfGold");
  }

}
