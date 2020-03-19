/**
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
