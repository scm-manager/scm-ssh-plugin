package com.cloudogu.scm.ssh;

import com.google.common.annotations.VisibleForTesting;
import sonia.scm.SCMContextProvider;
import sonia.scm.migration.UpdateStep;
import sonia.scm.plugin.Extension;
import sonia.scm.version.Version;

import javax.inject.Inject;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyPair;
import java.security.spec.InvalidKeySpecException;

@Extension
public class HostKeyAlgorithmUpdateStep implements UpdateStep {

  private final SCMContextProvider scmContextProvider;
  private final ConfigStore configStore;
  private File file;

  @Inject
  public HostKeyAlgorithmUpdateStep(SCMContextProvider scmContextProvider, ConfigStore configStore) {
    this.scmContextProvider = scmContextProvider;
    this.configStore = configStore;
  }

  @Override
  public void doUpdate() throws InvalidKeySpecException, IOException {
    if (file == null) {
      file = new File(scmContextProvider.getBaseDirectory().toPath().resolve("config").toFile(), "ssh-hostkeys.ser");
    }
    boolean fileExists = Files.exists(Paths.get(file.toURI()));
    if (fileExists) {
      try (ObjectInputStream is = new ObjectInputStream(Files.newInputStream(file.toPath()))) {
        readKeyPairs(is);
      }
    }
  }

  private void readKeyPairs(ObjectInputStream r) throws InvalidKeySpecException {
    KeyPair kp;
    try {
      kp = (KeyPair) r.readObject();
      if (kp.getPublic().getAlgorithm().equals(HostKeyAlgorithm.RSA.name())) {
        keepRsaAlgorithm();
      }
    } catch (ClassNotFoundException | IOException e) {
      throw new InvalidKeySpecException("Missing classes: " + e.getMessage(), e);
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

  @VisibleForTesting
  void setFile(File file) {
    this.file = file;
  }
}
