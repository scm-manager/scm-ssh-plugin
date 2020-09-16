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
package com.cloudogu.scm.ssh.accesstoken;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.otto.edison.hal.Link;
import de.otto.edison.hal.Links;
import org.apache.shiro.SecurityUtils;
import sonia.scm.config.ScmConfiguration;
import sonia.scm.protocolcommand.CommandContext;
import sonia.scm.protocolcommand.RepositoryContext;
import sonia.scm.protocolcommand.ScmCommandProtocol;
import sonia.scm.security.AccessToken;
import sonia.scm.security.AccessTokenBuilderFactory;

import javax.inject.Inject;
import java.io.IOException;

public class AccessTokenCommandProtocol implements ScmCommandProtocol {

  private static final ObjectMapper objectMapper = new ObjectMapper();

  private final AccessTokenBuilderFactory accessTokenBuilderFactory;
  private final ScmConfiguration scmConfiguration;

  @Inject
  public AccessTokenCommandProtocol(AccessTokenBuilderFactory accessTokenBuilderFactory, ScmConfiguration scmConfiguration) {
    this.accessTokenBuilderFactory = accessTokenBuilderFactory;
    this.scmConfiguration = scmConfiguration;
  }

  @Override
  public void handle(CommandContext context, RepositoryContext repositoryContext) throws IOException {
    AccessToken accessToken = createAccessToken();
    String indexUrl = createIndexUrl();

    objectMapper.writeValue(context.getOutputStream(), createCommandResult(accessToken, indexUrl));
  }

  AccessTokenCommandResult createCommandResult(AccessToken accessToken, String indexUrl) {
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
