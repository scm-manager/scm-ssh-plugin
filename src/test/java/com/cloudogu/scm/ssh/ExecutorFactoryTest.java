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
