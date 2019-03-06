package com.cloudogu.scm.ssh.command;

import com.cloudogu.scm.ssh.command.git.GitCommandInterpreterFactory;
import org.apache.sshd.common.session.Session;
import org.apache.sshd.common.util.threads.CloseableExecutorService;
import org.apache.sshd.server.ExitCallback;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import sonia.scm.protocolcommand.CommandContext;
import sonia.scm.protocolcommand.RepositoryContext;
import sonia.scm.protocolcommand.RepositoryContextResolver;
import sonia.scm.protocolcommand.git.GitCommandProtocol;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import static java.util.Collections.singleton;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ScmCommandTest {

  @Mock
  CloseableExecutorService executorService;
  @Mock
  RepositoryContextResolver repositoryContextResolver;
  @Mock
  GitCommandProtocol gitCommandProtocol;
  @Mock
  Session session;
  @Mock
  ExitCallback exitCallback;
  @Mock
  InputStream inputStream;
  @Mock
  OutputStream outputStream;
  @Mock
  OutputStream errorStream;

  @Captor
  ArgumentCaptor<CommandContext> commandContextCaptor;
  @Captor
  ArgumentCaptor<RepositoryContext> repositoryContextCaptor;

  @InjectMocks
  GitCommandInterpreterFactory gitCommandInterpreterFactory;

  ScmCommand scmCommand;

  @Test
  void shouldCallCapableInterpreter() throws IOException {
    initGitCommandProtocol();
    scmCommand = initTestObject("git arg");

    scmCommand.run();

    assertThat(commandContextCaptor.getValue().getArgs()).contains("git", "arg");
    assertThat(commandContextCaptor.getValue().getInputStream()).isSameAs(inputStream);
    assertThat(commandContextCaptor.getValue().getOutputStream()).isSameAs(outputStream);
    assertThat(commandContextCaptor.getValue().getErrorStream()).isSameAs(errorStream);

    assertThat(commandContextCaptor.getValue().getErrorStream()).isSameAs(errorStream);
    verify(exitCallback).onExit(0, "");
  }

  @Test
  void shouldFailIfNoInterpreterFound() throws IOException {
    scmCommand = initTestObject("other arg");

    scmCommand.run();

    verify(gitCommandProtocol, never()).handle(any(), any());
    verify(exitCallback).onExit(-1, "IllegalArgumentException");
  }

  void initGitCommandProtocol() throws IOException {
    doNothing().when(gitCommandProtocol).handle(commandContextCaptor.capture(), repositoryContextCaptor.capture());
  }

  ScmCommand initTestObject(String command) {
     scmCommand = new ScmCommand(command, executorService, singleton(gitCommandInterpreterFactory), repositoryContextResolver) {
      @Override
      public Session getSession() {
        return session;
      }
    };
    scmCommand.setExitCallback(exitCallback);
    scmCommand.setInputStream(inputStream);
    scmCommand.setOutputStream(outputStream);
    scmCommand.setErrorStream(errorStream);
    return scmCommand;
  }
}
