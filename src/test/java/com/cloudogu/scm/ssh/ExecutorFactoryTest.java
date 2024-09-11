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
import org.apache.shiro.mgt.DefaultSecurityManager;
import org.apache.shiro.subject.SimplePrincipalCollection;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.util.ThreadContext;
import org.apache.sshd.server.SshServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;

import static org.assertj.core.api.Assertions.assertThat;

class ExecutorFactoryTest {

  private Subject subject;

  @BeforeEach
  void setUpSubject() {
    subject = new Subject.Builder(new DefaultSecurityManager())
      .principals(new SimplePrincipalCollection("trillian", "test"))
      .buildSubject();

    ThreadContext.bind(subject);
  }

  @AfterEach
  void clearThreadContext() {
    ThreadContext.unbindSubject();
  }

  @Test
  void shouldPassSubjectToThreads() throws ExecutionException, InterruptedException {
    SshServer server = new SshServer();
    ExecutorFactory factory = new ExecutorFactory();
    factory.configure(server);

    ScheduledExecutorService scheduledExecutorService = server.getScheduledExecutorService();
    Future<Information> futureInformation = scheduledExecutorService.submit(this::createInformation);
    Information information = futureInformation.get();
    scheduledExecutorService.shutdown();

    assertThat(information.principal).isEqualTo("trillian");
    assertThat(information.threadName).startsWith("scm-ssh-");
  }

  private Information createInformation() {
    return new Information(
      SecurityUtils.getSubject().getPrincipal(),
      Thread.currentThread().getName()
    );
  }

  private class Information {
    private Object principal;
    private String threadName;

    private Information(Object principal, String threadName) {
      this.principal = principal;
      this.threadName = threadName;
    }
  }

}
