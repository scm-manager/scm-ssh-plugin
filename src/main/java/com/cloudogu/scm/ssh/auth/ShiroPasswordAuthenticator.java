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
package com.cloudogu.scm.ssh.auth;

import com.cloudogu.scm.ssh.ConfigStore;
import com.cloudogu.scm.ssh.Configuration;
import com.cloudogu.scm.ssh.SshServerConfigurator;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.sshd.server.SshServer;
import org.apache.sshd.server.auth.password.PasswordAuthenticator;
import org.apache.sshd.server.session.ServerSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;

public class ShiroPasswordAuthenticator implements PasswordAuthenticator, SshServerConfigurator {

  private static final Logger LOG = LoggerFactory.getLogger(ShiroPasswordAuthenticator.class);

  private final ConfigStore configStore;

  @Inject
  public ShiroPasswordAuthenticator(ConfigStore configStore) {
    this.configStore = configStore;
  }

  @Override
  public boolean authenticate(String username, String password, ServerSession session) {
    if (!isPasswordAuthenticationDisabled()) {
      return ShiroAuthenticator.authenticate(new UsernamePasswordToken(username, password), session);
    }
    return false;
  }

  private boolean isPasswordAuthenticationDisabled() {
    Configuration configuration = configStore.getConfiguration();
    return configuration.isDisablePasswordAuthentication();
  }

  @Override
  public void configure(SshServer server) {
    if (isPasswordAuthenticationDisabled()) {
      LOG.info("skip enabling ssh password authentication, because it is disabled by configuration");
    } else {
      LOG.debug("enable ssh password authentication");
      server.setPasswordAuthenticator(this);
    }
  }
}
