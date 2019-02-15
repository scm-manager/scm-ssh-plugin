package com.cloudogu.scm.ssh.auth;

import com.google.common.collect.Lists;
import com.google.inject.util.Providers;
import de.otto.edison.hal.HalRepresentation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import sonia.scm.api.v2.resources.ScmPathInfoStore;

import java.net.URI;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthorizedKeyCollectionMapperTest {

  private AuthorizedKeyCollectionMapper collectionMapper;

  @Mock
  private AuthorizedKeyMapper mapper;

  @BeforeEach
  void setUpObjectUnderTest() {
    ScmPathInfoStore pathInfoStore = new ScmPathInfoStore();
    pathInfoStore.set(() -> URI.create("/"));
    collectionMapper = new AuthorizedKeyCollectionMapper(Providers.of(pathInfoStore), mapper);
  }

  @Test
  void shouldMapToCollection() {
    when(mapper.map(any(AuthorizedKey.class))).then(ic -> new AuthorizedKeyDto());

    AuthorizedKey one = createAuthorizedKey("one");
    AuthorizedKey two = createAuthorizedKey("two");

    List<AuthorizedKey> keys = Lists.newArrayList(one, two);
    HalRepresentation collection = collectionMapper.map(keys);

    List<HalRepresentation> embedded = collection.getEmbedded().getItemsBy("keys");
    assertThat(embedded).hasSize(2);

    assertThat(collection.getLinks().getLinkBy("self").get().getHref()).isEqualTo("/v2/authorized_keys");
  }

  private AuthorizedKey createAuthorizedKey(String displayName) {
    AuthorizedKey one = new AuthorizedKey();
    one.setDisplayName(displayName);
    return one;
  }

}
