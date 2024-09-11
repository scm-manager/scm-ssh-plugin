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

import org.apache.shiro.SecurityUtils;
import sonia.scm.store.ConfigurationEntryStore;
import sonia.scm.store.ConfigurationEntryStoreFactory;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;

@Singleton
public class MeConfigStore {
  private final ConfigurationEntryStore<MeConfig> store;

  @Inject
  public MeConfigStore(ConfigurationEntryStoreFactory storeFactory) {
    this.store = storeFactory.withType(MeConfig.class).withName("sshPreferences").build();
  }

  public MeConfig get() {
    return store.getOptional(getUserId()).orElse(new MeConfig());
  }

  public void set(MeConfig meConfig) {
    store.put(getUserId(), meConfig);
  }


  private static String getUserId() {
    return SecurityUtils.getSubject().getPrincipal().toString();
  }
}
