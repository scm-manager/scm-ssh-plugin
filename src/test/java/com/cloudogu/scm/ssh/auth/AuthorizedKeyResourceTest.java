package com.cloudogu.scm.ssh.auth;

import de.otto.edison.hal.HalRepresentation;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.util.ThreadContext;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthorizedKeyResourceTest {

  @Mock
  private AuthorizedKeyStore store;

  @Mock
  private AuthorizedKeyCollectionMapper collectionMapper;

  @Mock
  private AuthorizedKeyMapper mapper;

  @InjectMocks
  private AuthorizedKeyResource resource;

  @Mock
  private Subject subject;

  @BeforeEach
  void setUpSubject() {
    when(subject.getPrincipal()).thenReturn("trillian");
    ThreadContext.bind(subject);
  }

  @AfterEach
  void clearSubject() {
    ThreadContext.unbindSubject();
  }

  @Test
  void shouldFindAll() {
    List<AuthorizedKey> keys = new ArrayList<>();
    when(store.getAll("trillian")).thenReturn(keys);

    HalRepresentation collection = new HalRepresentation();
    when(collectionMapper.map(keys)).thenReturn(collection);

    HalRepresentation result = resource.findAll();
    assertThat(result).isSameAs(collection);
  }

  @Test
  void shouldFindById() {
    AuthorizedKey key = new AuthorizedKey("42");
    when(store.findById("trillian", "42")).thenReturn(Optional.of(key));
    AuthorizedKeyDto dto = new AuthorizedKeyDto();
    when(mapper.map(key)).thenReturn(dto);

    Response response = resource.findById("42");
    assertThat(response.getStatus()).isEqualTo(200);
    assertThat(response.getEntity()).isSameAs(dto);
  }

  @Test
  void shouldReturn404IfIdDoesNotExists() {
    when(store.findById("trillian", "42")).thenReturn(Optional.empty());

    Response response = resource.findById("42");
    assertThat(response.getStatus()).isEqualTo(404);
  }

  @Test
  void shouldAddToStore() throws URISyntaxException {
    UriInfo uriInfo = mock(UriInfo.class);
    UriBuilder builder = mock(UriBuilder.class);
    when(uriInfo.getAbsolutePathBuilder()).thenReturn(builder);
    when(builder.path("42")).thenReturn(builder);
    when(builder.build()).thenReturn(new URI("/v2/authorized_keys/42"));

    AuthorizedKeyDto dto = new AuthorizedKeyDto();
    AuthorizedKey key = new AuthorizedKey();
    when(mapper.map(dto)).thenReturn(key);
    when(store.add("trillian", key)).thenReturn("42");

    Response response = resource.addKey(uriInfo, dto);

    assertThat(response.getStatus()).isEqualTo(201);
    assertThat(response.getLocation().toASCIIString()).isEqualTo("/v2/authorized_keys/42");
  }

  @Test
  void shouldDeleteFromStore() {
    Response response = resource.deleteById("42");
    assertThat(response.getStatus()).isEqualTo(204);
    verify(store).delete("trillian", "42");
  }
}
