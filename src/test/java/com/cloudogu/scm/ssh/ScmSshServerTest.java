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

import com.cloudogu.scm.ssh.auth.SshSecurityManager;
import com.google.inject.util.Providers;
import org.apache.shiro.util.ThreadContext;
import org.apache.sshd.server.SshServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import sonia.scm.plugin.PluginLoader;

import java.io.IOException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ScmSshServerTest {

  @Mock
  private SshSecurityManager securityManager;

  @Mock
  private SshServerConfigurator configurator;

  @Mock
  private PluginLoader pluginLoader;

  @Test
  void shouldCreateDefaultServer() {
    ScmSshServer scmSshServer = new ScmSshServer(
      Providers.of(securityManager), Collections.singleton(configurator), pluginLoader
    );

    assertThat(scmSshServer.getServer()).isNotNull();
  }

  @Test
  void shouldRunInScmSshServerThread() {
    ScmSshServer scmSshServer = new ScmSshServer(
      Providers.of(securityManager), Collections.singleton(configurator), pluginLoader
    );
    AtomicReference<String> threadName = new AtomicReference<>();
    scmSshServer.runInThread(() -> {
      threadName.set(Thread.currentThread().getName());
    });

    await()
      .atMost(1, TimeUnit.SECONDS)
      .untilAtomic(threadName, equalTo("ScmSshServer"));
  }

  @Nested
  class WithMockedServer {

    private ScmSshServer scmSshServer;

    private final List<SshServer> createdServers = new LinkedList<>();

    @BeforeEach
    void initTestObject() {
      scmSshServer = new ScmSshServer(Providers.of(securityManager), Collections.singleton(configurator), pluginLoader) {
        @Override
        void runInThread(Runnable r) {
          r.run();
        }

        @Override
        SshServer createDefaultServer() {
          SshServer serverMock = mock(SshServer.class);
          createdServers.add(serverMock);
          return serverMock;
        }
      };
    }

    @Test
    void shouldInitializeWithConfigurator() throws IOException {
      verify(configurator).configure(any());
      assertThat(createdServers).hasSize(1);
      verify(createdServers.get(0), never()).start();
    }

    @Test
    void shouldBindThreadContext() {
      scmSshServer.start();
      assertThat(ThreadContext.getSecurityManager()).isSameAs(securityManager);
    }

    @Test
    void shouldStartServer() throws IOException {
      scmSshServer.start();
      assertThat(createdServers).hasSize(1);
      verify(createdServers.get(0)).start();
    }

    @Test
    void shouldStopServer() throws IOException {
      scmSshServer.start();
      scmSshServer.stop();
      assertThat(createdServers).hasSize(1);
      verify(createdServers.get(0)).start();
      verify(createdServers.get(0)).stop();
    }

    @Test
    void shouldRestartServerOnPortChange() throws IOException {
      scmSshServer.start();

      scmSshServer.configurationChanged(createEvent(2222, 3333));

      assertRestart();
    }

    private void assertRestart() throws IOException {
      assertThat(createdServers).hasSize(2);
      verify(createdServers.get(0)).start();
      verify(createdServers.get(0)).stop();
      verify(createdServers.get(1)).start();
    }

    @Test
    void shouldRestartServerOnDisablePasswordAuthenticationChange() throws IOException {
      scmSshServer.start();

      ConfigChangedEvent event = createEvent(2222, 2222);
      event.getNewConfiguration().setDisablePasswordAuthentication(true);

      scmSshServer.configurationChanged(event);

      assertRestart();
    }

    @Test
    void shouldNotRestartServerWhenConfigurationChangedButNotPort() throws IOException {
      scmSshServer.start();

      scmSshServer.configurationChanged(createEvent(2222, 2222));

      assertThat(createdServers).hasSize(1);
      verify(createdServers.get(0)).start();
      verify(createdServers.get(0), never()).stop();
    }

    @AfterEach
    void cleanupThreadContext() {
      ThreadContext.unbindSecurityManager();
    }

    private ConfigChangedEvent createEvent(int oldPort, int newPort) {
      ConfigChangedEvent event = mock(ConfigChangedEvent.class);
      Configuration oldConfiguration = new Configuration();
      oldConfiguration.setPort(oldPort);
      when(event.getOldConfiguration()).thenReturn(oldConfiguration);
      Configuration newConfiguration = new Configuration();
      newConfiguration.setPort(newPort);
      when(event.getNewConfiguration()).thenReturn(newConfiguration);
      return event;
    }

  }
}
