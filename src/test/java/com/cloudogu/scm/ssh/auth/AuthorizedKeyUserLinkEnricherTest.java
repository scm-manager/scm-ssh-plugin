package com.cloudogu.scm.ssh.auth;

import com.google.inject.util.Providers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import sonia.scm.api.v2.resources.HalAppender;
import sonia.scm.api.v2.resources.HalEnricherContext;
import sonia.scm.api.v2.resources.ScmPathInfoStore;
import sonia.scm.user.User;
import sonia.scm.user.UserTestData;

import java.net.URI;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class AuthorizedKeyUserLinkEnricherTest {

  private AuthorizedKeyUserLinkEnricher linkEnricher;

  @Mock
  private HalAppender appender;

  @BeforeEach
  void setUpObjectUnderTest() {
    ScmPathInfoStore pathInfoStore = new ScmPathInfoStore();
    pathInfoStore.set(() -> URI.create("/"));

    linkEnricher = new AuthorizedKeyUserLinkEnricher(Providers.of(pathInfoStore));
  }

  @Test
  void shouldAppendLink() {
    User trillian = UserTestData.createTrillian();
    linkEnricher.enrich(HalEnricherContext.of(trillian), appender);
    verify(appender).appendLink("authorized_keys", "/v2/authorized_keys/trillian");
  }

}
