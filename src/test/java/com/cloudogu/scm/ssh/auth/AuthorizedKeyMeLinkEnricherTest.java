package com.cloudogu.scm.ssh.auth;

import com.google.inject.util.Providers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import sonia.scm.api.v2.resources.HalAppender;
import sonia.scm.api.v2.resources.ScmPathInfoStore;

import java.net.URI;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class AuthorizedKeyMeLinkEnricherTest {

  private AuthorizedKeyMeLinkEnricher enricher;

  @Mock
  private HalAppender appender;

  @BeforeEach
  void setUpObjectUnderTest() {
    ScmPathInfoStore pathInfoStore = new ScmPathInfoStore();
    pathInfoStore.set(() -> URI.create("/"));

    enricher = new AuthorizedKeyMeLinkEnricher(Providers.of(pathInfoStore));
  }

  @Test
  void shouldAppendAuthorizedKeysLink() {
    enricher.enrich(null, appender);

    verify(appender).appendLink("authorized_keys", "/v2/authorized_keys");
  }

}
