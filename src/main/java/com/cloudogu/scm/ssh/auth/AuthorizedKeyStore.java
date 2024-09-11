/*
 * Copyright (c) 2020 - present Cloudogu GmbH
 *
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, version 3.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see https://www.gnu.org/licenses/.
 */

package com.cloudogu.scm.ssh.auth;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import org.apache.sshd.common.config.keys.AuthorizedKeyEntry;
import sonia.scm.security.KeyGenerator;
import sonia.scm.store.DataStore;
import sonia.scm.store.DataStoreFactory;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
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
      throw new InvalidAuthorizedKeyException(ex);
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
