/*
 * Copyright (c) 2020 - present Cloudogu GmbH
 *
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, version 3.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see https://www.gnu.org/licenses/.
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
import sonia.scm.store.InMemoryByteConfigurationEntryStoreFactory;
import sonia.scm.store.InMemoryConfigurationStoreFactory;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SshProtocolProviderTest {

  @Mock
  private Subject subject;

  private ConfigStore sshConfigStore;
  private MeConfigStore meConfigStore;
  private SshProtocolProvider protocolProvider;
  private ScmConfiguration scmConfiguration;

  @BeforeEach
  void setUpObjectUnderTest() {
    ThreadContext.bind(subject);

    scmConfiguration = new ScmConfiguration();
    sshConfigStore = new ConfigStore(new InMemoryConfigurationStoreFactory(), scmConfiguration);
    meConfigStore = new MeConfigStore(new InMemoryByteConfigurationEntryStoreFactory());
    protocolProvider = new SshProtocolProvider(sshConfigStore, meConfigStore);
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
    assertThat(sshProtocol.isAnonymousEnabled()).isFalse();
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
    assertThat(sshProtocol.isAnonymousEnabled()).isFalse();
  }

  @Test
  void shouldReturnSshProtocolWithoutPort() {
    ScmProtocol sshProtocol = configureAndGet(22);

    assertThat(sshProtocol.getType()).isEqualTo("ssh");
    assertThat(sshProtocol.getUrl()).isEqualTo("ssh://scm.hitchhiker.com/repo/hitchhiker/HeartOfGold");
    assertThat(sshProtocol.isAnonymousEnabled()).isFalse();
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

  @Test
  void shouldReturnDefaultPriorityWhenNoStoreExists() {
    when(subject.getPrincipal()).thenReturn("trillian");

    ScmProtocol sshProtocol = configureAndGet(22);

    assertThat(sshProtocol.getPriority()).isEqualTo(100);
  }

  @Test
  void shouldReturnDefaultPriorityWhenSshIsNotPreferred() {
    when(subject.getPrincipal()).thenReturn("trillian");

    meConfigStore.set(new MeConfig(false));
    ScmProtocol sshProtocol = configureAndGet(22);

    assertThat(sshProtocol.getPriority()).isEqualTo(100);
  }

  @Test
  void shouldReturnIncreasedPriorityWhenSshIsPreferred() {
    when(subject.getPrincipal()).thenReturn("trillian");

    meConfigStore.set(new MeConfig(true));
    ScmProtocol sshProtocol = configureAndGet(22);

    assertThat(sshProtocol.getPriority()).isEqualTo(200);
  }

}
