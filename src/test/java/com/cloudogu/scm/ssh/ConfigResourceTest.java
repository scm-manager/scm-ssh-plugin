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
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

@SubjectAware(
  configuration = "classpath:com/cloudogu/scm/ssh/shiro.ini",
  password = "secret"
)
public class ConfigResourceTest {

  @Rule
  public ShiroRule shiro = new ShiroRule();

  @Mock
  private ScmPathInfoStore scmPathInfoStore;
  @Mock
  private ConfigStore configStore;
  @InjectMocks
  private ConfigurationMapperImpl configurationMapper;

  @Captor
  private ArgumentCaptor<Configuration> setCaptor;

  private ConfigResource resource;

  private RestDispatcher dispatcher;

  private static final Configuration CONFIGURATION = new Configuration();

  @Test
  @SubjectAware(username = "unpriv")
  public void unauthorizedUserShouldNotReadConfiguration() throws URISyntaxException {
    MockHttpRequest request = MockHttpRequest.get("/v2/config/ssh");
    MockHttpResponse response = new MockHttpResponse();

    dispatcher.invoke(request, response);

    assertEquals(403, response.getStatus());
  }

  @Test
  @SubjectAware(username = "reader")
  public void userWithReadPermissionShouldReadConfiguration() throws URISyntaxException, UnsupportedEncodingException {
    MockHttpRequest request = MockHttpRequest.get("/v2/config/ssh");
    MockHttpResponse response = new MockHttpResponse();

    dispatcher.invoke(request, response);

    assertEquals(200, response.getStatus());
    assertThat(response.getContentAsString()).contains("\"port\":2222");
    assertThat(response.getContentAsString()).contains("\"hostName\":\"localhost\"");
  }

  @Test
  @SubjectAware(username = "unpriv")
  public void unauthorizedUserShouldNotWriteConfiguration() throws URISyntaxException, IOException {
    MockHttpRequest request = MockHttpRequest
      .put("/v2/config/ssh")
      .content(readConfigJson())
      .contentType(MediaType.APPLICATION_JSON_TYPE);
    MockHttpResponse response = new MockHttpResponse();

    dispatcher.invoke(request, response);

    assertEquals(403, response.getStatus());
  }

  @Test
  @SubjectAware(username = "reader")
  public void userWithReadPermissionShouldNotWriteConfiguration() throws URISyntaxException, IOException {
    MockHttpRequest request = MockHttpRequest
      .put("/v2/config/ssh")
      .content(readConfigJson())
      .contentType(MediaType.APPLICATION_JSON_TYPE);
    MockHttpResponse response = new MockHttpResponse();

    dispatcher.invoke(request, response);

    assertEquals(403, response.getStatus());
  }

  @Test
  @SubjectAware(username = "writer")
  public void userWithWritePermissionShouldWriteConfiguration() throws URISyntaxException, IOException {
    MockHttpRequest request = MockHttpRequest
      .put("/v2/config/ssh")
      .content(readConfigJson())
      .contentType(MediaType.APPLICATION_JSON_TYPE);
    MockHttpResponse response = new MockHttpResponse();

    dispatcher.invoke(request, response);

    assertEquals(204, response.getStatus());

    assertEquals(3333, setCaptor.getValue().getPort());
    assertEquals("example.com", setCaptor.getValue().getHostName());
  }

  private byte[] readConfigJson() throws IOException {
    URL url = Resources.getResource("com/cloudogu/scm/ssh/config.json");
    return Resources.toByteArray(url);
  }

  @Before
  public void init() {
    initMocks(this);
    resource = new ConfigResource(configStore, configurationMapper);

    CONFIGURATION.setHostName("localhost");
    when(configStore.getConfiguration()).thenReturn(CONFIGURATION);
    doNothing().when(configStore).setConfiguration(setCaptor.capture());
    when(scmPathInfoStore.get()).thenReturn(() -> create("/"));

    dispatcher = new RestDispatcher();
    dispatcher.addSingletonResource(resource);
  }
}
