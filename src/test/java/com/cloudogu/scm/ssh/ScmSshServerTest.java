/**
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

import com.cloudogu.scm.ssh.auth.SshSecurityManager;
import com.google.inject.util.Providers;
import org.apache.shiro.util.ThreadContext;
import org.apache.sshd.server.SshServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
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

  private ScmSshServer scmSshServer;

  private List<SshServer> createdServers = new LinkedList<>();

  @BeforeEach
  void initTestObject() {
    scmSshServer = new ScmSshServer(Providers.of(securityManager), Collections.singleton(configurator)) {
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
