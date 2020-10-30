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

    @Override
    public boolean isAnonymousEnabled() {
      return false;
    }
  }
}
