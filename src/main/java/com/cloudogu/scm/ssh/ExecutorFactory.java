package com.cloudogu.scm.ssh;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.apache.shiro.concurrent.SubjectAwareScheduledExecutorService;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;

class ExecutorFactory {

  static ScheduledExecutorService create() {
    return new SubjectAwareScheduledExecutorService(
      Executors.newScheduledThreadPool(
        corePoolSize(),
        threadFactory()
      )
    );
  }

  private static ThreadFactory threadFactory() {
    return new ThreadFactoryBuilder().setNameFormat("scm-ssh-%s").build();
  }

  private static int corePoolSize() {
    return Runtime.getRuntime().availableProcessors();
  }

}
