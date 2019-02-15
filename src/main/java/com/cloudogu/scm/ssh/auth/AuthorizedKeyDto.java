package com.cloudogu.scm.ssh.auth;

import de.otto.edison.hal.HalRepresentation;
import de.otto.edison.hal.Links;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@SuppressWarnings("squid:S2160") // we do not need equals for dto
public class AuthorizedKeyDto extends HalRepresentation {

  private String displayName;
  private String raw;
  private Instant created;

  AuthorizedKeyDto(Links links) {
    super(links);
  }
}
