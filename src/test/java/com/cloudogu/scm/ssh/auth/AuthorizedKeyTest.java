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

import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

class AuthorizedKeyTest {

  @Test
  void shouldBeEqualById() {
    AuthorizedKey one = new AuthorizedKey("abc");
    one.setCreated(Instant.now());
    AuthorizedKey two = new AuthorizedKey("abc");
    two.setDisplayName("one two");

    assertThat(one).isEqualTo(two);
  }

  @Test
  void shouldHaveSameHashCodeForTheSameId() {
    AuthorizedKey one = new AuthorizedKey("123");
    one.setDisplayName("awesome");
    AuthorizedKey two = new AuthorizedKey("123");
    two.setDisplayName("one two");

    assertThat(one.hashCode()).isEqualTo(two.hashCode());
  }

}
