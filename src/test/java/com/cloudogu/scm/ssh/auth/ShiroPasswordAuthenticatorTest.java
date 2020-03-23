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
package com.cloudogu.scm.ssh.auth;

import com.cloudogu.scm.ssh.ConfigStore;
import com.cloudogu.scm.ssh.Configuration;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.util.ThreadContext;
import org.apache.sshd.server.SshServer;
import org.apache.sshd.server.session.ServerSession;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ShiroPasswordAuthenticatorTest {

  @Mock
  private ConfigStore configStore;

  @InjectMocks
  private ShiroPasswordAuthenticator shiroPasswordAuthenticator;

  private Configuration configuration;

  @BeforeEach
  void setUpConfiguration() {
    configuration = new Configuration();
    when(configStore.getConfiguration()).thenReturn(configuration);
  }

  @Nested
  class ConfigureServer {

    @Mock
    private SshServer server;

    @Test
    void shouldConfigureServer() {
      shiroPasswordAuthenticator.configure(server);
      verify(server).setPasswordAuthenticator(shiroPasswordAuthenticator);
    }

    @Test
    void shouldNotConfigurePasswordAuthenticationIfDisabled() {
      configuration.setDisablePasswordAuthentication(true);

      shiroPasswordAuthenticator.configure(server);
      verify(server, never()).setPasswordAuthenticator(shiroPasswordAuthenticator);
    }

  }

  @Nested
  class Authenticate {

    @Mock
    private ServerSession serverSession;

    @Mock
    private Subject subject;

    @BeforeEach
    void initSubject() {
      ThreadContext.bind(subject);
    }

    @Test
    void shouldAuthenticateWithPassword() {
      boolean authenticate = shiroPasswordAuthenticator.authenticate("user", "pw", serverSession);
      assertTrue(authenticate);
    }

    @Test
    void shouldReturnFalseIfWrongCredentials() {
      doThrow(AuthenticationException.class).when(subject).login(any());
      boolean authenticate = shiroPasswordAuthenticator.authenticate("user", "pw", serverSession);
      assertFalse(authenticate);
    }

    @Test
    void shouldSkipPasswordAuthenticationIfDisabled() {
      configuration.setDisablePasswordAuthentication(true);
      boolean authenticate = shiroPasswordAuthenticator.authenticate("user", "pw", serverSession);
      verify(subject, never()).login(any());
      assertFalse(authenticate);
    }

    @AfterEach
    void tearDownSubject() {
      ThreadContext.unbindSubject();
    }

  }

}
