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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import sonia.scm.SCMContextProvider;

import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class KeyPairConfiguratorTest {

  @Mock
  private SCMContextProvider context;
  @Mock
  private ConfigStore configStore;

  @InjectMocks
  private KeyPairConfigurator configurator;

  @Test
  void shouldApplyKeyProviderWithPathToConfig(@TempDir Path home) {
    when(context.resolve(any(Path.class))).then((ic) -> {
      Path path = ic.getArgument(0);
      return home.resolve(path);
    });

    SshServer server = new SshServer();
    configurator.configure(server);

    assertThat(server.getKeyPairProvider()).isInstanceOf(SimpleGeneratorHostKeyProvider.class);

    SimpleGeneratorHostKeyProvider provider = (SimpleGeneratorHostKeyProvider) server.getKeyPairProvider();

    Path path = provider.getPath();
    assertThat(path).isEqualByComparingTo( home.resolve("config").resolve("ssh-hostkeys.ser") );
  }

}
