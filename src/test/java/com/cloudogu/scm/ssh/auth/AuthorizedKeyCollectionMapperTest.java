/*
 * MIT License
 *
 * Copyright (c) 2020-present Cloudogu GmbH and Contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.cloudogu.scm.ssh.auth;

import com.google.common.collect.Lists;
import com.google.inject.util.Providers;
import de.otto.edison.hal.HalRepresentation;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.util.ThreadContext;
import org.junit.jupiter.api.AfterEach;
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
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthorizedKeyCollectionMapperTest {

  private AuthorizedKeyCollectionMapper collectionMapper;

  @Mock
  private AuthorizedKeyMapper mapper;

  @Mock
  private Subject subject;

  @BeforeEach
  void setUpObjectUnderTest() {
    ScmPathInfoStore pathInfoStore = new ScmPathInfoStore();
    pathInfoStore.set(() -> URI.create("/"));
    collectionMapper = new AuthorizedKeyCollectionMapper(Providers.of(pathInfoStore), mapper);

    ThreadContext.bind(subject);
  }

  @AfterEach
  void cleanThreadContext() {
    ThreadContext.unbindSubject();
  }

  @Test
  void shouldMapToCollection() {
    when(mapper.map(anyString(), any(AuthorizedKey.class))).then(ic -> new AuthorizedKeyDto());

    AuthorizedKey one = createAuthorizedKey("one");
    AuthorizedKey two = createAuthorizedKey("two");

    List<AuthorizedKey> keys = Lists.newArrayList(one, two);
    HalRepresentation collection = collectionMapper.map("trillian", keys);

    List<HalRepresentation> embedded = collection.getEmbedded().getItemsBy("keys");
    assertThat(embedded).hasSize(2);

    assertThat(collection.getLinks().getLinkBy("self").get().getHref()).isEqualTo("/v2/authorized_keys/trillian");
  }

  @Test
  void shouldAddCreateLinkIfTheUserIsPermitted() {
    when(subject.isPermitted("user:writeAuthorizedKeys:trillian")).thenReturn(true);

    HalRepresentation collection = collectionMapper.map("trillian", Lists.newArrayList());
    assertThat(collection.getLinks().getLinkBy("create").get().getHref()).isEqualTo("/v2/authorized_keys/trillian");
  }

  @Test
  void shouldNotAddCreateLinkWithoutPermission() {
    HalRepresentation collection = collectionMapper.map("trillian", Lists.newArrayList());
    assertThat(collection.getLinks().getLinkBy("create")).isNotPresent();
  }

  private AuthorizedKey createAuthorizedKey(String displayName) {
    AuthorizedKey one = new AuthorizedKey();
    one.setDisplayName(displayName);
    return one;
  }

}
