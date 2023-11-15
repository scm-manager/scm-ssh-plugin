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

import com.github.sdorra.shiro.ShiroRule;
import com.github.sdorra.shiro.SubjectAware;
import com.google.common.io.Resources;
import org.jboss.resteasy.mock.MockHttpRequest;
import org.jboss.resteasy.mock.MockHttpResponse;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import sonia.scm.api.v2.resources.ScmPathInfoStore;
import sonia.scm.web.RestDispatcher;

import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.net.URL;

import static java.net.URI.create;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@SubjectAware(
  configuration = "classpath:com/cloudogu/scm/ssh/shiro.ini",
  password = "secret"
)
public class MeConfigResourceTest {

  @Rule
  public ShiroRule shiro = new ShiroRule();

  @Mock
  private ScmPathInfoStore scmPathInfoStore;
  @Mock
  private MeConfigStore configStore;
  @InjectMocks
  private MeConfigMapperImpl configMapper;

  @Captor
  private ArgumentCaptor<MeConfig> setCaptor;

  private RestDispatcher dispatcher;

  private static final MeConfig CONFIGURATION = new MeConfig();

  @Test
  @SubjectAware(username = "unpriv")
  public void shouldReadSshPreferences() throws URISyntaxException, UnsupportedEncodingException {
    MockHttpRequest request = MockHttpRequest.get("/v2/me/sshPreferences");
    MockHttpResponse response = new MockHttpResponse();

    dispatcher.invoke(request, response);

    assertEquals(200, response.getStatus());
    assertThat(response.getContentAsString()).contains("\"useSshCheckout\":true");
  }

  @Test
  @SubjectAware(username = "unpriv")
  public void shouldWriteSshPreferences() throws URISyntaxException, IOException {
    URL url = Resources.getResource("com/cloudogu/scm/ssh/meConfig.json");
    MockHttpRequest request = MockHttpRequest
      .put("/v2/me/sshPreferences")
      .content(Resources.toByteArray(url))
      .contentType(MediaType.APPLICATION_JSON_TYPE);
    MockHttpResponse response = new MockHttpResponse();

    dispatcher.invoke(request, response);

    assertEquals(204, response.getStatus());
    assertTrue(setCaptor.getValue().isUseSshCheckout());
  }

  @Before
  public void init() {
    MockitoAnnotations.openMocks(this);
    MeConfigResource resource = new MeConfigResource(configStore, configMapper);

    CONFIGURATION.setUseSshCheckout(true);
    when(configStore.get()).thenReturn(CONFIGURATION);
    doNothing().when(configStore).set(setCaptor.capture());
    when(scmPathInfoStore.get()).thenReturn(() -> create("/"));

    dispatcher = new RestDispatcher();
    dispatcher.addSingletonResource(resource);
  }
}
