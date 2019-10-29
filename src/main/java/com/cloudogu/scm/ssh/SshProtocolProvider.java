package com.cloudogu.scm.ssh;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
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
    private int sshPort;
    private NamespaceAndName namespaceAndName;

    private SshProtocol(String serverName, int sshPort, NamespaceAndName namespaceAndName) {
      this.serverName = serverName;
      this.sshPort = sshPort;
      this.namespaceAndName = namespaceAndName;
    }

    @Override
    public String getType() {
      return "ssh";
    }

    @Override
    public String getUrl() {
      StringBuilder builder = new StringBuilder("ssh://");

      Subject subject = SecurityUtils.getSubject();
      if (subject.isAuthenticated()) {
        builder.append(subject.getPrincipal()).append("@");
      }

      String hostname = serverName;
      if (hostname.startsWith("ssh://")) {
        hostname = hostname.substring(6);
      }

      int portIndex = hostname.indexOf(':');
      if (portIndex > 0) {
        builder.append(hostname, 0, portIndex);
        int port = Integer.parseInt(hostname.substring(portIndex + 1));
        if (port != 22) {
          builder.append(":").append(port);
        }
      } else {
        builder.append(hostname);
        if (sshPort != 22) {
          builder.append(":")
            .append(sshPort);
        }
      }

      return builder.append("/repo/")
        .append(namespaceAndName.getNamespace())
        .append("/")
        .append(namespaceAndName.getName())
        .toString();
    }
  }
}
