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
import com.cloudogu.scm.ssh.simplecommand.SimpleCommandContext;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.otto.edison.hal.Link;
import de.otto.edison.hal.Links;
import org.apache.shiro.SecurityUtils;
import sonia.scm.config.ScmConfiguration;
import sonia.scm.protocolcommand.CommandContext;
import sonia.scm.security.AccessToken;
import sonia.scm.security.AccessTokenBuilderFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class AccessTokenCommand implements SimpleCommand {

  private static final ObjectMapper objectMapper = new ObjectMapper();
  private static final int SUCCESS_EXIT_CODE = 0;

  private final AccessTokenBuilderFactory accessTokenBuilderFactory;
  private final ScmConfiguration scmConfiguration;

  public AccessTokenCommand(AccessTokenBuilderFactory accessTokenBuilderFactory, ScmConfiguration scmConfiguration) {
    this.accessTokenBuilderFactory = accessTokenBuilderFactory;
    this.scmConfiguration = scmConfiguration;
  }

  @Override
  public int execute(SimpleCommandContext context) throws IOException {
    AccessToken accessToken = createAccessToken();
    String indexUrl = createIndexUrl();

    String json = objectMapper.writeValueAsString(createCommandResult(accessToken, indexUrl));
    context.getOutputStream().write(json.getBytes(StandardCharsets.UTF_8));
    return SUCCESS_EXIT_CODE;
  }

  private AccessTokenCommandResult createCommandResult(AccessToken accessToken, String indexUrl) {
    return new AccessTokenCommandResult(
      accessToken.compact(),
      Links.linkingTo().single(Link.link("index", indexUrl)).build()
    );
  }

  private AccessToken createAccessToken() {
    return accessTokenBuilderFactory
      .create()
      .subject(SecurityUtils.getSubject().getPrincipal().toString())
      .build();
  }

  private String createIndexUrl() {
    String baseUrl = scmConfiguration.getBaseUrl();
    if (!baseUrl.endsWith("/")) {
      baseUrl += "/";
    }
    return baseUrl + "api/v2";
  }
}
