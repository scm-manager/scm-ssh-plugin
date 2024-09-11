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

import sonia.scm.SCMContextProvider;
import sonia.scm.migration.UpdateStep;
import sonia.scm.plugin.Extension;
import sonia.scm.version.Version;

import jakarta.inject.Inject;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.KeyPair;
import java.security.spec.InvalidKeySpecException;

@Extension
public class HostKeyAlgorithmUpdateStep implements UpdateStep {

  private final SCMContextProvider scmContextProvider;
  private final ConfigStore configStore;

  @Inject
  public HostKeyAlgorithmUpdateStep(SCMContextProvider scmContextProvider, ConfigStore configStore) {
    this.scmContextProvider = scmContextProvider;
    this.configStore = configStore;
  }

  @Override
  public void doUpdate() throws InvalidKeySpecException, IOException {
    Path path = scmContextProvider.resolve(Paths.get("config")).resolve("ssh-hostkeys.ser");
    if (Files.exists(path)) {
      String algorithm = readKeyPairAlgorithm(path);
      if (HostKeyAlgorithm.RSA.name().equalsIgnoreCase(algorithm)) {
        keepRsaAlgorithm();
      }
    }
  }

  private String readKeyPairAlgorithm(Path path) throws InvalidKeySpecException, IOException {
    try (ObjectInputStream is = new ObjectInputStream(Files.newInputStream(path))) {
      KeyPair kp;
      try {
        kp = (KeyPair) is.readObject();
        return kp.getPublic().getAlgorithm();
      } catch (ClassNotFoundException | IOException e) {
        throw new InvalidKeySpecException("Missing classes: " + e.getMessage(), e);
      }
    }
  }

  private void keepRsaAlgorithm() {
    Configuration configuration = configStore.getConfiguration();
    configuration.setAlgorithm(HostKeyAlgorithm.RSA);
    configStore.setConfiguration(configuration);
  }

  @Override
  public Version getTargetVersion() {
    return Version.parse("2.3.3");
  }

  @Override
  public String getAffectedDataType() {
    return "ssh-hostkeys.ser";
  }
}
