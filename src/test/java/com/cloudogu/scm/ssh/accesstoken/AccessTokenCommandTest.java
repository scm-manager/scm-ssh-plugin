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
package com.cloudogu.scm.ssh.accesstoken;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.util.ThreadContext;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Answers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import sonia.scm.config.ScmConfiguration;
import sonia.scm.protocolcommand.CommandContext;
import sonia.scm.security.AccessToken;
import sonia.scm.security.AccessTokenBuilder;
import sonia.scm.security.AccessTokenBuilderFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AccessTokenCommandTest {

  @Mock
  private Subject subject;
  @Mock
  private ScmConfiguration scmConfiguration;
  @Mock
  private CommandContext context;
  @Mock
  private AccessTokenBuilderFactory tokenBuilderFactory;
  @Mock(answer = Answers.RETURNS_SELF)
  private AccessTokenBuilder tokenBuilder;
  @Mock
  private AccessToken token;

  @InjectMocks
  private AccessTokenCommand accessTokenCommand;

  @BeforeEach
  void initSubject() {
    ThreadContext.bind(subject);
  }

  @AfterEach
  void tearDownSubject() {
    ThreadContext.unbindSubject();
  }

  @Test
  void shouldReturnValidAccessTokenAndIndex() throws IOException {
    String compactToken = "token123";
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    when(subject.getPrincipal()).thenReturn("trillian");
    when(scmConfiguration.getBaseUrl()).thenReturn("baseurl/");
    when(context.getOutputStream()).thenReturn(baos);
    when(tokenBuilderFactory.create()).thenReturn(tokenBuilder);
    when(tokenBuilder.build()).thenReturn(token);
    when(token.compact()).thenReturn(compactToken);

    accessTokenCommand.execute(context);

    JsonNode result = new ObjectMapper().readTree(baos.toByteArray());

    assertThat(result.get("accessToken").textValue()).isEqualTo(compactToken);
    assertThat(result.get("_links").get("index").get("href").textValue()).isEqualTo("baseurl/api/v2");
  }
}
