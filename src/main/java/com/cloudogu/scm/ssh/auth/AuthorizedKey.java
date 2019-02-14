package com.cloudogu.scm.ssh.auth;

import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
class AuthorizedKey {
  private String displayName;
  private String authorizedKey;
  private Instant created;
}
