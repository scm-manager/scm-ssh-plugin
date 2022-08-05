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

import sonia.scm.SCMContextProvider;
import sonia.scm.migration.UpdateStep;
import sonia.scm.plugin.Extension;
import sonia.scm.version.Version;

import javax.inject.Inject;
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
