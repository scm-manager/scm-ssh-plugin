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
import java.nio.file.Paths;
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
    when(scmContextProvider.resolve(any())).thenReturn(Paths.get(Resources.getResource("com/cloudogu/scm/ssh/ec").toURI()));

    updateStep.doUpdate();

    verify(configStore, never()).setConfiguration(any());
  }

  @Test
  void shouldKeepRsaAlgorithm() throws InvalidKeySpecException, IOException, URISyntaxException {
    when(scmContextProvider.resolve(any())).thenReturn(Paths.get(Resources.getResource("com/cloudogu/scm/ssh/rsa").toURI()));
    when(configStore.getConfiguration()).thenReturn(new Configuration());

    updateStep.doUpdate();

    verify(configStore).setConfiguration(argThat(arg -> {
      assertThat(arg.getAlgorithm()).isEqualTo(HostKeyAlgorithm.RSA);
      return true;
    }));
  }

  @Test
  void shouldDoNothingIfNoHostKeysFound() throws URISyntaxException, InvalidKeySpecException, IOException {
    when(scmContextProvider.resolve(any())).thenReturn(Paths.get(Resources.getResource("com/cloudogu/scm/ssh").toURI()));

    updateStep.doUpdate();

    verify(configStore, never()).setConfiguration(any());
  }
}
