package com.cloudogu.scm.ssh.auth;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import org.apache.sshd.common.config.keys.AuthorizedKeyEntry;
import sonia.scm.security.KeyGenerator;
import sonia.scm.store.DataStore;
import sonia.scm.store.DataStoreFactory;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Singleton
class AuthorizedKeyStore {

  private static final String STORE_NAME = "authorized_keys";

  private final KeyGenerator keyGenerator;
  private final DataStore<AuthorizedKeys> store;

  @Inject
  public AuthorizedKeyStore(KeyGenerator keyGenerator, DataStoreFactory dataStoreFactory) {
    this.keyGenerator = keyGenerator;
    this.store = dataStoreFactory.withType(AuthorizedKeys.class).withName(STORE_NAME).build();
  }

  Optional<AuthorizedKey> findById(String username, String id) {
    AuthorizedKeys keys = getKeysForUser(username);
    return Optional.ofNullable(keys.findById(id));
  }

  List<AuthorizedKey> getAll(String username) {
    AuthorizedKeys authorizedKeys = getKeysForUser(username);
    return ImmutableList.copyOf(authorizedKeys.keys);
  }

  List<AuthorizedKey> getAllWithoutPermissionCheck(String username) {
    return getKeysForUserWithoutPermissionCheck(username).keys;
  }

  String add(String username, AuthorizedKey authorizedKey) {
    AuthorizedKeys authorizedKeys = getKeysForUser(username);

    Permissions.checkWriteAuthorizedKeys((username));

    normalize(authorizedKey);
    validate(authorizedKey);

    String id = keyGenerator.createKey();
    authorizedKey.setId(id);
    authorizedKey.setCreated(Instant.now());
    authorizedKeys.add(authorizedKey);
    store.put(username, authorizedKeys);
    return id;
  }

  private void normalize(AuthorizedKey authorizedKey) {
    authorizedKey.setRaw(Strings.nullToEmpty(authorizedKey.getRaw()).trim());
  }

  private void validate(AuthorizedKey authorizedKey) {
    try {
      AuthorizedKeyEntry.parseAuthorizedKeyEntry(authorizedKey.getRaw());
    } catch (IllegalArgumentException ex) {
      throw new InvalidAuthorizedKeyException("invalid or unsupported key", ex);
    }
  }

  void delete(String username, String key) {
    AuthorizedKeys authorizedKeys = getKeysForUser(username);

    Permissions.checkWriteAuthorizedKeys(username);

    authorizedKeys.delete(key);
    store.put(username, authorizedKeys);
  }

  private AuthorizedKeys getKeysForUser(String username) {
    Permissions.checkReadAuthorizedKeys(username);
    return getKeysForUserWithoutPermissionCheck(username);
  }

  private AuthorizedKeys getKeysForUserWithoutPermissionCheck(String username) {
    AuthorizedKeys authorizedKeys = store.get(username);
    if (authorizedKeys == null) {
      authorizedKeys = new AuthorizedKeys();
    }
    return authorizedKeys;
  }

  @XmlRootElement
  @XmlAccessorType(value = XmlAccessType.FIELD)
  private static class AuthorizedKeys {
    @XmlElement(name = "key")
    private List<AuthorizedKey> keys = new ArrayList<>();

    private AuthorizedKey findById(String id) {
      for (AuthorizedKey key : keys) {
        if (id.equals(key.getId())) {
          return key;
        }
      }
      return null;
    }

    private void add(AuthorizedKey authorizedKey) {
      keys.add(authorizedKey);
    }

    private void delete(String id) {
      keys.remove(new AuthorizedKey(id));
    }
  }
}
