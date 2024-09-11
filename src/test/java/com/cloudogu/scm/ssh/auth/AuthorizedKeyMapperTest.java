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
