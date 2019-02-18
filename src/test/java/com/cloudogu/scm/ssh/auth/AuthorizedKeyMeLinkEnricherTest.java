package com.cloudogu.scm.ssh.auth;

import com.google.inject.util.Providers;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.util.ThreadContext;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import sonia.scm.api.v2.resources.HalAppender;
import sonia.scm.api.v2.resources.ScmPathInfoStore;

import java.net.URI;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthorizedKeyMeLinkEnricherTest {

  private AuthorizedKeyMeLinkEnricher enricher;

  @Mock
  private Subject subject;

  @Mock
  private HalAppender appender;

  @BeforeEach
  void setUpObjectUnderTest() {
    ScmPathInfoStore pathInfoStore = new ScmPathInfoStore();
    pathInfoStore.set(() -> URI.create("/"));

    enricher = new AuthorizedKeyMeLinkEnricher(Providers.of(pathInfoStore));

    ThreadContext.bind(subject);
  }

  @AfterEach
  void clearThreadContext() {
    ThreadContext.unbindSubject();
  }

  @Test
  void shouldAppendAuthorizedKeysLink() {
    when(subject.getPrincipal()).thenReturn("trillian");

    enricher.enrich(null, appender);

    verify(appender).appendLink("authorized_keys", "/v2/authorized_keys/trillian");
  }

}
