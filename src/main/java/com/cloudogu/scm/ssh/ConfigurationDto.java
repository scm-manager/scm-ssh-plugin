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
public class ConfigurationDto extends HalRepresentation {

  private String hostName;
  @Range(min = 0, max = 49151)
  private int port;

  public ConfigurationDto(Links links) {
    super(links);
  }
}
