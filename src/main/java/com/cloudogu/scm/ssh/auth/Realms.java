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

package com.cloudogu.scm.ssh.auth;

import com.google.inject.Inject;
import com.google.inject.Injector;
import org.apache.shiro.realm.Realm;
import sonia.scm.plugin.PluginLoader;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

class Realms {

  private final Injector injector;
  private final PluginLoader pluginLoader;

  @Inject
  public Realms(Injector injector, PluginLoader pluginLoader) {
    this.injector = injector;
    this.pluginLoader = pluginLoader;
  }

  Collection<Realm> get() {
    // TODO check if we should expose realm in scm-webapp, instead of creating a new set of realms
    List<Realm> realms = new ArrayList<>();
    Iterable<Class<? extends Realm>> classes = pluginLoader.getExtensionProcessor().byExtensionPoint(Realm.class);
    for (Class<? extends Realm> realmClass : classes) {
      Realm realm = injector.getInstance(realmClass);
      realms.add(realm);
    }
    return realms;
  }

}
