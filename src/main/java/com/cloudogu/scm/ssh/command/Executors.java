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
package com.cloudogu.scm.ssh.command;

import org.apache.shiro.concurrent.SubjectAwareExecutorService;
import org.apache.sshd.common.future.CloseFuture;
import org.apache.sshd.common.future.SshFutureListener;
import org.apache.sshd.common.util.threads.CloseableExecutorService;
import org.apache.sshd.common.util.threads.ThreadUtils;

final class Executors {

  private Executors() {
  }

  static CloseableExecutorService get() {
    // TODO
    // we create a new executor for every command, this sounds not right
    // but with a cached thread pool the server hangs on the second request
    // Looks like there is no support to pool threads beyond the CloseableExecutorService#shutdown
    // method, which itself calls close.
    // And it does not seem to be a good idea to fetch shutdown calls and
    // prevent them from being processed.
    return subjectAware(ThreadUtils.newSingleThreadExecutor("ScmSshCommand"));
  }

  private static CloseableExecutorService subjectAware(CloseableExecutorService executorService) {
    return new SubjectAwareCloseableExecutorService(executorService);
  }

  private static class SubjectAwareCloseableExecutorService extends SubjectAwareExecutorService implements CloseableExecutorService {

    private final CloseableExecutorService target;

    private SubjectAwareCloseableExecutorService(CloseableExecutorService target) {
      super(target);
      this.target = target;
    }

    @Override
    public CloseFuture close(boolean immediately) {
      return target.close(immediately);
    }

    @Override
    public void addCloseFutureListener(SshFutureListener<CloseFuture> listener) {
      target.addCloseFutureListener(listener);
    }

    @Override
    public void removeCloseFutureListener(SshFutureListener<CloseFuture> listener) {
      target.removeCloseFutureListener(listener);
    }

    @Override
    public boolean isClosed() {
      return target.isClosed();
    }

    @Override
    public boolean isClosing() {
      return target.isClosing();
    }
  }
}
