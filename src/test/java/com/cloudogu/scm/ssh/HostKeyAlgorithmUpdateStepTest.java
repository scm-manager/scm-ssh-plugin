package com.cloudogu.scm.ssh;

import com.google.common.io.Resources;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import sonia.scm.SCMContextProvider;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.security.spec.InvalidKeySpecException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class HostKeyAlgorithmUpdateStepTest {

  @Mock
  private ConfigStore configStore;
  @Mock
  private SCMContextProvider scmContextProvider;

  @InjectMocks
  private HostKeyAlgorithmUpdateStep updateStep;

  @Test
  void shouldNotKeepAlgorithm() throws InvalidKeySpecException, IOException, URISyntaxException {
    updateStep.setFile(new File(Resources.getResource("com/cloudogu/scm/ssh/ec-hostkeys.ser").toURI()));

    updateStep.doUpdate();

    verify(configStore, never()).setConfiguration(any());
  }

  @Test
  void shouldKeepRsaAlgorithm() throws InvalidKeySpecException, IOException, URISyntaxException {
    when(configStore.getConfiguration()).thenReturn(new Configuration());
    updateStep.setFile(new File(Resources.getResource("com/cloudogu/scm/ssh/rsa-hostkeys.ser").toURI()));

    updateStep.doUpdate();

    verify(configStore).setConfiguration(argThat(arg -> {
      assertThat(arg.getAlgorithm()).isEqualTo(HostKeyAlgorithm.RSA);
      return true;
    }));
  }
}
