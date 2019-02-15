package com.cloudogu.scm.ssh.auth;

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
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthorizedKeyStoreTest {

  @Mock
  private KeyGenerator keyGenerator;

  private AtomicInteger counter;

  private DataStoreFactory dataStoreFactory;

  private AuthorizedKeyStore keyStore;

  @BeforeEach
  void setUpObjectUnderTest() {
    dataStoreFactory = new InMemoryDataStoreFactory();
    keyStore = new AuthorizedKeyStore(keyGenerator, dataStoreFactory);
  }

  @Nested
  class StoringTests {

    @BeforeEach
    private void setUpKeyGenerator() {
      counter = new AtomicInteger();
      when(keyGenerator.createKey()).then(ic -> String.valueOf(counter.incrementAndGet()));
    }

    @Test
    void shouldStore() {
      AuthorizedKey key = new AuthorizedKey();
      key.setDisplayName("one");

      String id = keyStore.add("trillian", key);
      Optional<AuthorizedKey> optionalKey = keyStore.findById("trillian", id);
      assertThat(optionalKey).isPresent();

      AuthorizedKey one = optionalKey.get();
      assertThat(one.getId()).isEqualTo(id);
      assertThat(one.getCreated()).isNotNull();
    }

    @Test
    void shouldDelete() {
      AuthorizedKey key = new AuthorizedKey();
      key.setDisplayName("one");

      String id = keyStore.add("trillian", key);
      keyStore.delete("trillian", id);

      Optional<AuthorizedKey> optionalKey = keyStore.findById("trillian", id);
      assertThat(optionalKey).isNotPresent();
    }

    @Test
    void shouldFindAllOfOneUser() {
      keyStore.add("trillian", new AuthorizedKey());
      keyStore.add("trillian", new AuthorizedKey());
      keyStore.add("dent", new AuthorizedKey());

      List<AuthorizedKey> keys = keyStore.getAll("trillian");
      assertThat(keys).hasSize(2);
    }
  }

  @Test
  void shouldReturnEmpty() {
    Optional<AuthorizedKey> optionalKey = keyStore.findById("trillian", "42");
    assertThat(optionalKey).isNotPresent();
  }

}
