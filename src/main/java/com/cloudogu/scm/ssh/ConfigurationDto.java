package com.cloudogu.scm.ssh;

import de.otto.edison.hal.HalRepresentation;
import de.otto.edison.hal.Links;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Range;

@Getter
@Setter
@NoArgsConstructor
@SuppressWarnings("squid:S2160") // no need for equals and hash code
public class ConfigurationDto extends HalRepresentation {

  @SshHostnameWithPort
  private String hostName;

  @Range(min = 1, max = 65535)
  private int port;

  private boolean disablePasswordAuthentication;

  public ConfigurationDto(Links links) {
    super(links);
  }
}
