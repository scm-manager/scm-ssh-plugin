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
