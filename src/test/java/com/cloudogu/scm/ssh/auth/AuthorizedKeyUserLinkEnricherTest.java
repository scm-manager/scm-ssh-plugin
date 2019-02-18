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
import sonia.scm.api.v2.resources.HalEnricherContext;
import sonia.scm.api.v2.resources.ScmPathInfoStore;
import sonia.scm.user.User;
import sonia.scm.user.UserTestData;

import java.net.URI;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthorizedKeyUserLinkEnricherTest {

  private AuthorizedKeyUserLinkEnricher linkEnricher;

  @Mock
  private HalAppender appender;

  @Mock
  private Subject subject;

  @BeforeEach
  void setUpObjectUnderTest() {
    ScmPathInfoStore pathInfoStore = new ScmPathInfoStore();
    pathInfoStore.set(() -> URI.create("/"));

    linkEnricher = new AuthorizedKeyUserLinkEnricher(Providers.of(pathInfoStore));

    ThreadContext.bind(subject);
  }

  @AfterEach
  void clearThreadContext() {
    ThreadContext.unbindSubject();
  }

  @Test
  void shouldAppendLink() {
    when(subject.isPermitted("user:readAuthorizedKeys:trillian")).thenReturn(true);

    User trillian = UserTestData.createTrillian();
    linkEnricher.enrich(HalEnricherContext.of(trillian), appender);

    verify(appender).appendLink("authorized_keys", "/v2/authorized_keys/trillian");
  }

  @Test
  void shouldNotAppendLinkWithoutPermission() {
    User trillian = UserTestData.createTrillian();
    linkEnricher.enrich(HalEnricherContext.of(trillian), appender);

    verify(appender, never()).appendLink("authorized_keys", "/v2/authorized_keys/trillian");
  }

}
