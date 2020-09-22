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
package com.cloudogu.scm.ssh.command;

import com.cloudogu.scm.ssh.simplecommand.SimpleCommandFactory;
import org.apache.sshd.common.session.Session;
import org.apache.sshd.common.util.threads.CloseableExecutorService;
import org.apache.sshd.server.ExitCallback;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import sonia.scm.protocolcommand.CommandContext;
import sonia.scm.protocolcommand.CommandInterpreter;
import sonia.scm.protocolcommand.CommandInterpreterFactory;
import sonia.scm.protocolcommand.RepositoryContext;
import sonia.scm.protocolcommand.RepositoryContextResolver;
import sonia.scm.protocolcommand.ScmCommandProtocol;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Optional;

import static java.util.Collections.singleton;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ScmCommandTest {

  @Mock
  private CloseableExecutorService executorService;
  @Mock
  private ScmCommandProtocol gitCommandProtocol;
  @Mock
  private Session session;
  @Mock
  private ExitCallback exitCallback;
  @Mock
  private InputStream inputStream;
  @Mock
  private OutputStream outputStream;
  @Mock
  private OutputStream errorStream;

  @Captor
  private ArgumentCaptor<CommandContext> commandContextCaptor;

  @Mock
  private RepositoryContextResolver repositoryContextResolver;

  @Captor
  private ArgumentCaptor<RepositoryContext> repositoryContextCaptor;

  @Mock
  private CommandInterpreterFactory commandInterpreterFactory;

  @Mock
  private SimpleCommandFactory simpleCommandFactory;

  @Mock
  private CommandInterpreter commandInterpreter;

  @Test
  void shouldCallCapableInterpreter() throws IOException {
    ScmCommand scmCommand = initGitCommandProtocol("git arg");
    scmCommand.run();

    assertThat(commandContextCaptor.getValue().getArgs()).contains("git", "arg");
    assertThat(commandContextCaptor.getValue().getInputStream()).isSameAs(inputStream);
    assertThat(commandContextCaptor.getValue().getOutputStream()).isSameAs(outputStream);
    assertThat(commandContextCaptor.getValue().getErrorStream()).isSameAs(errorStream);

    assertThat(commandContextCaptor.getValue().getErrorStream()).isSameAs(errorStream);
    verify(exitCallback).onExit(0, "");
  }

  @Test
  void shouldCallCapableSimpleCommand() {
    when(simpleCommandFactory.canHandle("simple-command")).thenReturn(Optional.of(context -> 0));
    ScmCommand scmCommand = initTestObject("simple-command");

    scmCommand.run();

    verify(simpleCommandFactory).canHandle("simple-command");
  }

  @Test
  void shouldFailIfNoInterpreterFound() throws IOException {
    ScmCommand scmCommand = initTestObject("other arg");
    scmCommand.run();

    verify(gitCommandProtocol, never()).handle(any(), any());
    verify(exitCallback).onExit(-1, "IllegalArgumentException");
  }

  ScmCommand initGitCommandProtocol(String command) throws IOException {
    when(commandInterpreter.getParsedArgs()).then(ic -> command.split("\\s"));
    when(commandInterpreter.getRepositoryContextResolver()).thenReturn(repositoryContextResolver);
    when(commandInterpreter.getProtocolHandler()).thenReturn(gitCommandProtocol);

    doNothing().when(gitCommandProtocol).handle(commandContextCaptor.capture(), repositoryContextCaptor.capture());

    when(commandInterpreterFactory.canHandle(command)).thenReturn(Optional.of(commandInterpreter));
    return initTestObject(command);
  }

  ScmCommand initTestObject(String command) {
    ScmCommand scmCommand = new ScmCommand(command, executorService, singleton(commandInterpreterFactory), singleton(simpleCommandFactory)) {
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
