package com.cloudogu.scm.ssh;

import org.apache.sshd.server.SshServer;

public interface SshServerConfigurator {

  void configure(SshServer server);

}
