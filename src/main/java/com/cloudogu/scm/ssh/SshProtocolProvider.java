package com.cloudogu.scm.ssh;

import com.google.common.base.Strings;
import sonia.scm.config.ScmConfiguration;
import sonia.scm.plugin.Extension;
import sonia.scm.repository.NamespaceAndName;
import sonia.scm.repository.Repository;
import sonia.scm.repository.api.ScmProtocol;
import sonia.scm.repository.api.ScmProtocolProvider;

import javax.inject.Inject;
import java.net.URI;

@Extension
public class SshProtocolProvider implements ScmProtocolProvider {

  private final Configuration sshConfiguration;
  private final ScmConfiguration scmConfiguration;

  @Inject
  public SshProtocolProvider(Configuration sshConfiguration, ScmConfiguration scmConfiguration) {
    this.sshConfiguration = sshConfiguration;
    this.scmConfiguration = scmConfiguration;
  }

  @Override
  public String getType() {
    return "git";
  }

  @Override
  public SshProtocol get(Repository repository) {
    if ("git".equals(repository.getType())) {
      String serverName = serverNameFromBaseUrl();
      return new SshProtocol(serverName, sshConfiguration.getPort(), repository.getNamespaceAndName());
    }
    return null;
  }

  private String serverNameFromBaseUrl() {
    String baseUrl = scmConfiguration.getBaseUrl();
    if (Strings.isNullOrEmpty(baseUrl)) {
      // it is possible, that is empty? Is localhost the best option we have?
      return "localhost";
    }

    return URI.create(baseUrl).getHost();
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
