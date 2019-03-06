package com.cloudogu.scm.ssh;

import com.cloudogu.scm.ssh.auth.SshSecurityManager;
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
    scmSshServer = new ScmSshServer(securityManager, Collections.singleton(configurator)) {
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
  void shouldBindThreadContext() throws IOException {
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
  void shouldRestartServerOnConfigurationChange() throws IOException {
    scmSshServer.start();

    scmSshServer.configurationChanged(createEvent(2222, 3333));

    assertThat(createdServers).hasSize(2);
    verify(createdServers.get(0)).start();
    verify(createdServers.get(0)).stop();
    verify(createdServers.get(1)).start();
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
    when(event.getOldItem()).thenReturn(oldConfiguration);
    Configuration newConfiguration = new Configuration();
    newConfiguration.setPort(newPort);
    when(event.getItem()).thenReturn(newConfiguration);
    return event;
  }
}
