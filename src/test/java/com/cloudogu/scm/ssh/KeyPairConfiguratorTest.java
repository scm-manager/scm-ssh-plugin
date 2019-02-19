package com.cloudogu.scm.ssh;

import org.apache.sshd.server.SshServer;
import org.apache.sshd.server.keyprovider.SimpleGeneratorHostKeyProvider;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junitpioneer.jupiter.TempDirectory;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import sonia.scm.SCMContextProvider;

import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith({MockitoExtension.class, TempDirectory.class})
class KeyPairConfiguratorTest {

  @Mock
  private SCMContextProvider context;

  @InjectMocks
  private KeyPairConfigurator configurator;

  @Test
  void shouldApplyKeyProviderWithPathToConfig(@TempDirectory.TempDir Path home) {
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
