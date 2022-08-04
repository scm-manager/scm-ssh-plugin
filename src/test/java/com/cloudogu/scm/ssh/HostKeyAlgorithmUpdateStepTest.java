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
