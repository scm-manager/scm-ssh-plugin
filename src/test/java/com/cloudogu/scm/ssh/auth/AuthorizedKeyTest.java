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
