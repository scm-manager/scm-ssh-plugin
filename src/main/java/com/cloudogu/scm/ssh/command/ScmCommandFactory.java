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
package com.cloudogu.scm.ssh.command;

import com.cloudogu.scm.ssh.SshServerConfigurator;
import com.cloudogu.scm.ssh.simplecommand.SimpleCommandFactory;
import org.apache.sshd.server.SshServer;
import org.apache.sshd.server.command.AbstractDelegatingCommandFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sonia.scm.protocolcommand.CommandInterpreterFactory;

import javax.inject.Inject;
import java.util.Set;

public class ScmCommandFactory extends AbstractDelegatingCommandFactory implements SshServerConfigurator {

  private static final Logger LOG = LoggerFactory.getLogger(ScmCommandFactory.class);

  private final Set<CommandInterpreterFactory> commandInterpreterFactories;
  private final Set<SimpleCommandFactory> simpleCommandFactories;

  @Inject
  public ScmCommandFactory(Set<CommandInterpreterFactory> commandInterpreterFactories, Set<SimpleCommandFactory> simpleCommandFactories) {
    super(ScmCommandFactory.class.getSimpleName());
    this.commandInterpreterFactories = commandInterpreterFactories;
    this.simpleCommandFactories = simpleCommandFactories;
  }

  @Override
  public boolean isSupportedCommand(String command) {
    return true;
  }

  @Override
  protected ScmCommand executeSupportedCommand(String command) {
    LOG.debug("create scm command for '{}'", command);
    return new ScmCommand(command, Executors.get(), commandInterpreterFactories, simpleCommandFactories);
  }

  @Override
  public void configure(SshServer server) {
    server.setCommandFactory(this);
  }
}
