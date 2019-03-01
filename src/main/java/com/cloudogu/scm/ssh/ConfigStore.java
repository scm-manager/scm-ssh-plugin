package com.cloudogu.scm.ssh;

import com.google.common.base.Strings;
import sonia.scm.config.ScmConfiguration;
import sonia.scm.event.ScmEventBus;
import sonia.scm.store.ConfigurationStore;
import sonia.scm.store.ConfigurationStoreFactory;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.net.URI;

@Singleton
public class ConfigStore {

  private final ConfigurationStore<Configuration> store;
  private final ScmConfiguration scmConfiguration;

  @Inject
  public ConfigStore(ConfigurationStoreFactory storeFactory, ScmConfiguration scmConfiguration) {
    this.store = storeFactory.withType(Configuration.class).withName("ssh").build();
    this.scmConfiguration = scmConfiguration;
  }

  public Configuration getConfiguration() {
    return store.getOptional().orElse(new Configuration());
  }

  public void setConfiguration(Configuration newConfig) {
    Configuration oldConfig = this.getConfiguration();
    this.store.set(newConfig);
    ScmEventBus.getInstance().post(new ConfigChangedEvent(oldConfig, newConfig));
  }

  public String getBaseUrl() {
    return Strings.isNullOrEmpty(getConfiguration().getHostName()) ?
      serverNameFromBaseUrl() :
      getConfiguration().getHostName();
  }

  private String serverNameFromBaseUrl() {
    String baseUrl = scmConfiguration.getBaseUrl();
    if (Strings.isNullOrEmpty(baseUrl)) {
      // it is possible, that is empty? Is localhost the best option we have?
      return "localhost";
    }

    URI uri = URI.create(baseUrl);
    return uri.getHost();
  }

  public int getPort() {
    return getConfiguration().getPort();
  }
}
