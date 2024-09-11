/*
 * Copyright (c) 2020 - present Cloudogu GmbH
 *
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, version 3.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see https://www.gnu.org/licenses/.
 */

package com.cloudogu.scm.ssh;

import com.google.common.base.Strings;
import sonia.scm.config.ScmConfiguration;
import sonia.scm.event.ScmEventBus;
import sonia.scm.store.ConfigurationStore;
import sonia.scm.store.ConfigurationStoreFactory;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
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

  public String getAlgorithm() {
    return getConfiguration().getAlgorithm().toString();
  }
}
