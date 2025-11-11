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

import com.google.common.base.Strings;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import sonia.scm.plugin.Extension;
import sonia.scm.repository.NamespaceAndName;
import sonia.scm.repository.Repository;
import sonia.scm.repository.api.ScmProtocol;
import sonia.scm.repository.api.ScmProtocolProvider;

import jakarta.inject.Inject;

@Extension
public class SshProtocolProvider implements ScmProtocolProvider {

  private final ConfigStore sshConfigStore;

  private final MeConfigStore meConfigStore;

  @Inject
  public SshProtocolProvider(ConfigStore sshConfigStore, MeConfigStore meConfigStore) {
    this.sshConfigStore = sshConfigStore;
    this.meConfigStore = meConfigStore;
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
      if (Strings.isNullOrEmpty(hostname)) {
        hostname = "localhost";
      }

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

    @Override
    public boolean isAnonymousEnabled() {
      return false;
    }

    @Override
    public int getPriority() {
      if (meConfigStore.get().isUseSshCheckout()) {
        return 200;
      }
      return 100;
    }
  }
}
