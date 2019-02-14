package com.cloudogu.scm.ssh.command;

import java.io.IOException;

public interface ScmSshProtocol {

  void handle(CommandContext context, RepositoryContext repositoryContext) throws IOException;

}
