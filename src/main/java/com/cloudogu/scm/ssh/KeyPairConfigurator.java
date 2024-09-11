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

import org.apache.sshd.server.SshServer;
import org.apache.sshd.server.keyprovider.SimpleGeneratorHostKeyProvider;
import sonia.scm.SCMContextProvider;

import jakarta.inject.Inject;
import java.nio.file.Path;
import java.nio.file.Paths;

public class KeyPairConfigurator implements SshServerConfigurator {

  private final SCMContextProvider context;
  private final ConfigStore configStore;

  @Inject
  public KeyPairConfigurator(SCMContextProvider context, ConfigStore configStore) {
    this.context = context;
    this.configStore = configStore;
  }

  @Override
  public void configure(SshServer server) {
    Path path = context.resolve(Paths.get("config", "ssh-hostkeys.ser"));
    SimpleGeneratorHostKeyProvider keyProvider = new SimpleGeneratorHostKeyProvider(path);
    keyProvider.setAlgorithm(configStore.getAlgorithm());
    server.setKeyPairProvider(keyProvider);
  }
}
