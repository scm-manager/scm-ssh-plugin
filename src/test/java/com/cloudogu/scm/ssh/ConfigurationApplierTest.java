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
import org.junit.jupiter.api.Test;
import sonia.scm.store.InMemoryConfigurationStoreFactory;

import static org.assertj.core.api.Assertions.assertThat;

class ConfigurationApplierTest {

  @Test
  void shouldApplyConfiguration() {
    Configuration configuration = new Configuration();
    configuration.setPort(42);

    ConfigStore configStore = new ConfigStore(new InMemoryConfigurationStoreFactory(), null);
    configStore.setConfiguration(configuration);

    ConfigurationApplier applier = new ConfigurationApplier(configStore);

    SshServer server = new SshServer();
    applier.configure(server);

    assertThat(server.getPort()).isEqualTo(42);
  }

}
