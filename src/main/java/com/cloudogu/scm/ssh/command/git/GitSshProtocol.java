package com.cloudogu.scm.ssh.command.git;

import com.cloudogu.scm.ssh.command.RepositoryContext;
import com.cloudogu.scm.ssh.command.CommandContext;
import com.cloudogu.scm.ssh.command.ScmSshProtocol;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.lib.RepositoryCache;
import org.eclipse.jgit.transport.ReceivePack;
import org.eclipse.jgit.transport.RemoteConfig;
import org.eclipse.jgit.transport.UploadPack;
import org.eclipse.jgit.util.FS;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sonia.scm.repository.RepositoryPermissions;

import javax.inject.Inject;
import java.io.IOException;

public class GitSshProtocol implements ScmSshProtocol {

  private static final Logger LOG = LoggerFactory.getLogger(GitSshProtocol.class);

  private SshUploadPackFactory uploadPackFactory;
  private SshReceivePackFactory receivePackFactory;

  @Inject
  public GitSshProtocol(SshUploadPackFactory uploadPackFactory, SshReceivePackFactory receivePackFactory) {
    this.uploadPackFactory = uploadPackFactory;
    this.receivePackFactory = receivePackFactory;
  }

  @Override
  public void handle(CommandContext commandContext, RepositoryContext repositoryContext) throws IOException {
    String subCommand = commandContext.getArgs()[0];

    if (RemoteConfig.DEFAULT_UPLOAD_PACK.equals(subCommand)) {
      LOG.trace("got upload pack");
      upload(commandContext, repositoryContext);
    } else if (RemoteConfig.DEFAULT_RECEIVE_PACK.equals(subCommand)) {
      LOG.trace("got receive pack");
      receive(commandContext, repositoryContext);
    } else {
      throw new IllegalArgumentException("Unknown git command: " + commandContext.getCommand());
    }
  }

  private void receive(CommandContext commandContext, RepositoryContext repositoryContext) throws IOException {
    RepositoryPermissions.push(repositoryContext.getRepository()).check();
    try (Repository repository = open(repositoryContext)) {
      ReceivePack receivePack = receivePackFactory.create(repositoryContext, repository);
      receivePack.receive(commandContext.getInputStream(), commandContext.getOutputStream(), commandContext.getErrorStream());
    }
  }

  private void upload(CommandContext commandContext, RepositoryContext repositoryContext) throws IOException {
    RepositoryPermissions.pull(repositoryContext.getRepository()).check();
    try (Repository repository = open(repositoryContext)) {
      UploadPack uploadPack = uploadPackFactory.create(repositoryContext, repository);
      uploadPack.upload(commandContext.getInputStream(), commandContext.getOutputStream(), commandContext.getErrorStream());
    }
  }

  private Repository open(RepositoryContext repositoryContext) throws IOException {
    RepositoryCache.FileKey key = RepositoryCache.FileKey.lenient(repositoryContext.getDirectory().toFile(), FS.DETECTED);
    return key.open(true);
  }

}
