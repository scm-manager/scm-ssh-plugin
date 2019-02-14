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
