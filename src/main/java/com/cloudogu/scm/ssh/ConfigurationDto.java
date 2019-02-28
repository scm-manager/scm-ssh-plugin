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

  private String hostName;
  @Range(min = 1, max = 0xBFFF)
  private int port;

  public ConfigurationDto(Links links) {
    super(links);
  }
}
