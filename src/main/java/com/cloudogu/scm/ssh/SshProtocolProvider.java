package com.cloudogu.scm.ssh;

import sonia.scm.plugin.Extension;
import sonia.scm.repository.NamespaceAndName;
import sonia.scm.repository.Repository;
import sonia.scm.repository.api.ScmProtocol;
import sonia.scm.repository.api.ScmProtocolProvider;

import javax.inject.Inject;

@Extension
public class SshProtocolProvider implements ScmProtocolProvider {

  private final ConfigStore sshConfigStore;

  @Inject
  public SshProtocolProvider(ConfigStore sshConfigStore) {
    this.sshConfigStore = sshConfigStore;
  }

  @Override
  public String getType() {
    return "git";
  }

  @Override
  public SshProtocol get(Repository repository) {
    if ("git".equals(repository.getType())) {
      String serverName = sshConfigStore.getBaseUrl();
      return new SshProtocol(serverName, sshConfigStore.getPort(), repository.getNamespaceAndName());
    }
    return null;
  }

  public class SshProtocol implements ScmProtocol {

    private String serverName;
    private int port;
    private NamespaceAndName namespaceAndName;

    private SshProtocol(String serverName, int port, NamespaceAndName namespaceAndName) {
      this.serverName = serverName;
      this.port = port;
      this.namespaceAndName = namespaceAndName;
    }

    @Override
    public String getType() {
      return "ssh";
    }

    @Override
    public String getUrl() {
      return String.format(
        "ssh://%s:%d/repo/%s/%s",
        serverName,
        port,
        namespaceAndName.getNamespace(),
        namespaceAndName.getName()
      );
    }
  }
}
