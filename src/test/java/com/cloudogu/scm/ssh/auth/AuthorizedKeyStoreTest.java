package com.cloudogu.scm.ssh.auth;

import com.google.common.collect.Lists;
import org.apache.shiro.authz.AuthorizationException;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.util.ThreadContext;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import sonia.scm.security.KeyGenerator;
import sonia.scm.store.DataStoreFactory;
import sonia.scm.store.InMemoryDataStoreFactory;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthorizedKeyStoreTest {

  private static final String VALID_RAW_KEY = "ssh-rsa AAAAB3NzaC1yc2EAAAADAQABAAABAQCsts5J8omrz5H9JP52Gnd6G2n3hy" +
    "/YPjQ0JCQTjO3iBkfzTm83dzti5DhZ20migHhZblhjv8sPk3e/+xVI/II5KejalI0ODBQ9YSkDsFHNlelrK2kbwXVEcLN8S8is7l6312wej2" +
    "PAMQdDRsbjRGRRXbymzO4ZWfHVlIEuZAkBeNZS8K66nXX98cj6n6e8ZP0VCV/FRD82TDFD9Zvj19rkp3pJjJaOIftJ92op62P/g6po/+0bF9" +
    "maCtIO6umgzuA1LiWaUXPEsxNIAkaZwiw2RgekqpWqkYibQOW2YCL67MwOf9WWWXgXmRNnS5gCRdYuXPLP23kF0HdRauJUwqV " +
    "trillian@heart-of-gold";

  @Mock
  private Subject subject;

  @Mock
  private KeyGenerator keyGenerator;

  private AtomicInteger counter;

  private DataStoreFactory dataStoreFactory;

  private AuthorizedKeyStore keyStore;

  @BeforeEach
  void setUpObjectUnderTest() {
    dataStoreFactory = new InMemoryDataStoreFactory();
    keyStore = new AuthorizedKeyStore(keyGenerator, dataStoreFactory);

    ThreadContext.bind(subject);
  }

  @AfterEach
  void clearThreadContext() {
    ThreadContext.unbindSubject();
  }

  private AuthorizedKey createValidAuthorizedKey() {
    AuthorizedKey authorizedKey = new AuthorizedKey();
    authorizedKey.setRaw(VALID_RAW_KEY);
    return authorizedKey;
  }

  private AuthorizedKey createInvalidAuthorizedKey() {
    AuthorizedKey authorizedKey = new AuthorizedKey();
    authorizedKey.setRaw("abc");
    return authorizedKey;
  }

  @Nested
  class StoringTests {

    @BeforeEach
    void setUpKeyGenerator() {
      counter = new AtomicInteger();
      when(keyGenerator.createKey()).then(ic -> String.valueOf(counter.incrementAndGet()));
    }

    @Test
    void shouldStore() {
      AuthorizedKey key = createValidAuthorizedKey();
      key.setDisplayName("one");

      String id = keyStore.add("trillian", key);
      Optional<AuthorizedKey> optionalKey = keyStore.findById("trillian", id);
      assertThat(optionalKey).isPresent();

      AuthorizedKey one = optionalKey.get();
      assertThat(one.getId()).isEqualTo(id);
      assertThat(one.getCreated()).isNotNull();
    }

    @Test
    void shouldTrimStoredKeys() {
      AuthorizedKey key = new AuthorizedKey();
      key.setRaw("   " + VALID_RAW_KEY + "   ");
      key.setDisplayName("one");

      String id = keyStore.add("trillian", key);
      Optional<AuthorizedKey> optionalKey = keyStore.findById("trillian", id);
      assertThat(optionalKey).isPresent();

      assertThat(optionalKey.get().getRaw()).isEqualTo(VALID_RAW_KEY);
    }

    @Test
    void shouldDelete() {
      AuthorizedKey key = createValidAuthorizedKey();
      key.setDisplayName("one");

      String id = keyStore.add("trillian", key);
      keyStore.delete("trillian", id);

      Optional<AuthorizedKey> optionalKey = keyStore.findById("trillian", id);
      assertThat(optionalKey).isNotPresent();
    }

    @Test
    void shouldFindAllOfOneUser() {
      keyStore.add("trillian", createValidAuthorizedKey());
      keyStore.add("trillian", createValidAuthorizedKey());
      keyStore.add("dent", createValidAuthorizedKey());

      List<AuthorizedKey> keys = keyStore.getAll("trillian");
      assertThat(keys).hasSize(2);
    }
  }

  @Test
  void shouldCheckPermissionForGetAll() {
    doThrowAuthorizationException("user:readAuthorizedKeys:trillian");

    assertThrows(AuthorizationException.class, () -> keyStore.getAll("trillian"));
  }

  @Test
  void shouldCheckPermissionForFindById() {
    doThrowAuthorizationException("user:readAuthorizedKeys:trillian");

    assertThrows(AuthorizationException.class, () -> keyStore.findById("trillian", "one"));
  }

  @Test
  void shouldCheckPermissionForAdd() {
    doThrowAuthorizationException("user:writeAuthorizedKeys:trillian");
    assertThrows(AuthorizationException.class, () -> keyStore.add("trillian", createValidAuthorizedKey()));
  }

  @Test
  void shouldCheckPermissionForDelete() {
    doThrowAuthorizationException("user:writeAuthorizedKeys:trillian");
    assertThrows(AuthorizationException.class, () -> keyStore.delete("trillian", "42"));
  }

  private void doThrowAuthorizationException(String... permissionArgs) {
    List<String> permissions = Lists.newArrayList(permissionArgs);
    doAnswer(ic -> {
      String permission = ic.getArgument(0);
      if (permissions.contains(permission)) {
        throw new AuthorizationException("missing permission " + permission);
      }
      return null;
    }).when(subject).checkPermission(anyString());
  }

  @Test
  void shouldReturnEmpty() {
    Optional<AuthorizedKey> optionalKey = keyStore.findById("trillian", "42");
    assertThat(optionalKey).isNotPresent();
  }

  @Test
  void shouldThrowExceptionForInvalidKeys() {
    assertThrows(InvalidAuthorizedKeyException.class, () -> keyStore.add("trillian", createInvalidAuthorizedKey()));
  }

}
