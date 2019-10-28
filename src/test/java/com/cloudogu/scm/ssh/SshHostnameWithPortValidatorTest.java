package com.cloudogu.scm.ssh;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class SshHostnameWithPortValidatorTest {

  private final SshHostnameWithPortValidator validator = new SshHostnameWithPortValidator();

  @Test
  void shouldReturnTrueForNullValue() {
    assertThat(validator.isValid(null, null)).isTrue();
  }

  @Test
  void shouldReturnFalseForInvalidHosts() {
    String[] values = new String[]{
      "http://ssh.cloudogu.com",
      "ftp://ssh.cloudogu.com",
      "ssh/cloudogu/com",
      "ssh.cloudogu.com:4a2",
      "ssh cloudogu com",
      "ssh:cloudogu:com"
    };
    for (String v : values) {
      assertThat(validator.isValid(v, null)).isFalse();
    }
  }

  @Test
  void shouldReturnTrueForValidHosts() {
    String[] values = new String[]{
      "ssh://ssh.cloudogu.com",
      "ssh://ssh.cloudogu.com:22",
      "ssh://ssh.cloudogu.com:2222",
      "192.168.22.22:2222",
      "192.168.22.22"
    };
    for (String v : values) {
      assertThat(validator.isValid(v, null)).isTrue();
    }
  }

}
