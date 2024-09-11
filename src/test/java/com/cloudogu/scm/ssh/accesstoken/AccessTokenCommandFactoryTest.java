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

package com.cloudogu.scm.ssh.accesstoken;

import com.cloudogu.scm.ssh.simplecommand.SimpleCommand;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class AccessTokenCommandFactoryTest {

  @InjectMocks
  private AccessTokenCommandFactory factory;

  @Test
  void shouldReturnEmptyOptional() {
    assertThat(factory.canHandle("invalid")).isEmpty();
  }

  @Test
  void shouldReturnInterpreter() {
    Optional<SimpleCommand> commandInterpreter = factory.canHandle("scm-access-token");
    assertThat(commandInterpreter).isNotEmpty();
    assertThat(commandInterpreter.get()).isInstanceOf(AccessTokenCommand.class);
  }
}
