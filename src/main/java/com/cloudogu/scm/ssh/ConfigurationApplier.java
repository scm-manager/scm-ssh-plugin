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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.inject.Inject;

public class ConfigurationApplier implements SshServerConfigurator {

  private static final Logger LOG = LoggerFactory.getLogger(ConfigurationApplier.class);

  private final ConfigStore configStore;

  @Inject
  public ConfigurationApplier(ConfigStore configStore) {
    this.configStore = configStore;
  }

  @Override
  public void configure(SshServer server) {
    int port = configStore.getConfiguration().getPort();
    LOG.info("setting ssh server port to {}", port);
    server.setPort(port);
  }
}
