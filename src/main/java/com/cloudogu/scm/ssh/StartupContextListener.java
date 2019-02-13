package com.cloudogu.scm.ssh;

import sonia.scm.EagerSingleton;
import sonia.scm.plugin.Extension;

import javax.inject.Inject;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

@Extension
@EagerSingleton
public class StartupContextListener implements ServletContextListener {

  private final ScmSshServer server;

  @Inject
  public StartupContextListener(ScmSshServer server) {
    this.server = server;
  }

  @Override
  public void contextInitialized(ServletContextEvent sce) {
    server.start();
  }

  @Override
  public void contextDestroyed(ServletContextEvent sce) {
    server.stop();
  }
}
