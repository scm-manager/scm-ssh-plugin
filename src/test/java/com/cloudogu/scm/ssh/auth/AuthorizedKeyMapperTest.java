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

import com.google.inject.util.Providers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.junit.jupiter.MockitoExtension;
import sonia.scm.api.v2.resources.ScmPathInfoStore;

import java.net.URI;
import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class AuthorizedKeyMapperTest {

  private AuthorizedKeyMapper mapper;

  @BeforeEach
  void setUpObjectUnderTest() {
    ScmPathInfoStore pathInfoStore = new ScmPathInfoStore();
    pathInfoStore.set(() -> URI.create("/"));

    mapper = Mappers.getMapper(AuthorizedKeyMapper.class);
    mapper.setScmPathInfoStore(Providers.of(pathInfoStore));
  }

  @Test
  void shouldMapToDto() {
    AuthorizedKey key = new AuthorizedKey("42");
    key.setDisplayName("Yubikey");
    key.setRaw("ssh-rsa ... cardno:1234");
    key.setCreated(Instant.now());

    AuthorizedKeyDto dto = mapper.map("trillian", key);
    assertThat(dto.getDisplayName()).isEqualTo(key.getDisplayName());
    assertThat(dto.getRaw()).isEqualTo(key.getRaw());
    assertThat(dto.getCreated()).isEqualTo(key.getCreated());
  }

  @Test
  void shouldAppendSelfLink() {
    AuthorizedKey key = new AuthorizedKey("42");
    AuthorizedKeyDto dto = mapper.map("trillian", key);
    assertThat(dto.getLinks().getLinkBy("self").get().getHref()).isEqualTo("/v2/authorized_keys/trillian/42");
  }

  @Test
  void shouldAppendDeleteLink() {
    AuthorizedKey key = new AuthorizedKey("42");
    AuthorizedKeyDto dto = mapper.map("trillian", key);
    assertThat(dto.getLinks().getLinkBy("delete").get().getHref()).isEqualTo("/v2/authorized_keys/trillian/42");
  }

  @Test
  void shouldMapFromDto() {
    AuthorizedKeyDto dto = new AuthorizedKeyDto();
    dto.setDisplayName("Yubikey");
    dto.setRaw("ssh-rsa ... cardno:1234");
    dto.setCreated(Instant.now());

    AuthorizedKey key = mapper.map(dto);
    assertThat(key.getDisplayName()).isEqualTo(dto.getDisplayName());
    assertThat(key.getRaw()).isEqualTo(dto.getRaw());
    assertThat(key.getCreated()).isEqualTo(dto.getCreated());
  }

}
