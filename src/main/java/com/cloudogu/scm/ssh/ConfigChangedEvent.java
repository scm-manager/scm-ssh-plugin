package com.cloudogu.scm.ssh;

import sonia.scm.event.Event;

@Event
public class ConfigChangedEvent {
  private final Configuration oldConfiguration;
  private final Configuration newConfiguration;

  ConfigChangedEvent(Configuration oldConfiguration, Configuration newConfiguration) {
    this.oldConfiguration = oldConfiguration;
    this.newConfiguration = newConfiguration;
  }

  public Configuration getOldConfiguration() {
    return oldConfiguration;
  }

  public Configuration getNewConfiguration() {
    return newConfiguration;
  }
}
