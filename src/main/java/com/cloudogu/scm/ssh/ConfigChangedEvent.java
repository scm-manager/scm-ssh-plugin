package com.cloudogu.scm.ssh;

import sonia.scm.HandlerEventType;
import sonia.scm.event.Event;
import sonia.scm.event.HandlerEvent;

@Event
public class ConfigChangedEvent implements HandlerEvent<Configuration> {
  private final Configuration oldConfiguration;
  private final Configuration newConfiguration;

  public ConfigChangedEvent(Configuration oldConfiguration, Configuration newConfiguration) {
    this.oldConfiguration = oldConfiguration;
    this.newConfiguration = newConfiguration;
  }

  @Override
  public HandlerEventType getEventType() {
    return HandlerEventType.MODIFY;
  }

  @Override
  public Configuration getItem() {
    return oldConfiguration;
  }

  @Override
  public Configuration getOldItem() {
    return newConfiguration;
  }
}
