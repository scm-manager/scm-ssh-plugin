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
