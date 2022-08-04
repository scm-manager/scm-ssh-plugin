/*
 * MIT License
 *
 * Copyright (c) 2020-present Cloudogu GmbH and Contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
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

  public String getAlgorithm() {
    return getConfiguration().getAlgorithm().toString();
  }
}
