package com.cloudogu.scm.ssh.auth;

import com.google.common.collect.ImmutableList;
import org.apache.shiro.SecurityUtils;
import sonia.scm.store.DataStore;
import sonia.scm.store.DataStoreFactory;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Singleton
class AuthorizedKeyStore {

  private static final String STORE = "authorized_keys";

  private DataStore<AuthorizedKeys> store;

  @Inject
  public AuthorizedKeyStore(DataStoreFactory dataStoreFactory) {
    this.store = dataStoreFactory.withType(AuthorizedKeys.class).withName(STORE).build();
  }

  Iterable<AuthorizedKey> getAll(String username) {
    AuthorizedKeys userPublicKeyStore = store.get(username);
    if (userPublicKeyStore != null) {
      return ImmutableList.copyOf(userPublicKeyStore.keys);
    }
    return Collections.emptySet();
  }

  void add(String username, AuthorizedKey authorizedKey) {
    AuthorizedKeys authorizedKeys = store.get(username);
    if (authorizedKeys == null) {
      authorizedKeys = new AuthorizedKeys();
      authorizedKeys.add(authorizedKey);
    }
    store.put(username, authorizedKeys);
  }

  @XmlRootElement
  @XmlAccessorType(value = XmlAccessType.FIELD)
  private static class AuthorizedKeys {
    @XmlElement(name = "key")
    private List<AuthorizedKey> keys = new ArrayList<>();

    private void add(AuthorizedKey authorizedKey) {
      keys.add(authorizedKey);
    }
  }
}
