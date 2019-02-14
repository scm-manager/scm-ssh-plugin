package com.cloudogu.scm.ssh;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.apache.shiro.concurrent.SubjectAwareScheduledExecutorService;
import org.apache.sshd.server.SshServer;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;

class ExecutorFactory implements SshServerConfigurator {

  @Override
  public void configure(SshServer server) {
    server.setScheduledExecutorService(create(), true);
  }

  private ScheduledExecutorService create() {
    return new SubjectAwareScheduledExecutorService(
      Executors.newScheduledThreadPool(
        corePoolSize(),
        threadFactory()
      )
    );
  }

  private ThreadFactory threadFactory() {
    return new ThreadFactoryBuilder().setNameFormat("scm-ssh-%s").build();
  }

  private int corePoolSize() {
    return Runtime.getRuntime().availableProcessors();
  }
}
