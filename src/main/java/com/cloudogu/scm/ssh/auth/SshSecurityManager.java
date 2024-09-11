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

package com.cloudogu.scm.ssh.auth;

import org.apache.shiro.mgt.DefaultSecurityManager;
import org.apache.shiro.mgt.DefaultSessionStorageEvaluator;
import org.apache.shiro.mgt.DefaultSubjectDAO;

import jakarta.inject.Inject;

/**
 * SecurityManager used for ssh authentication and authorization. The {@link SshSecurityManager} should be injected as
 * a {@link jakarta.inject.Provider} to avoid circular dependency resolution problems.
 */
@SuppressWarnings("squid:MaximumInheritanceDepth")
public class SshSecurityManager extends DefaultSecurityManager {

  @Inject
  public SshSecurityManager(Realms realms) {
    super(realms.get());

    DefaultSubjectDAO subjectDAO = new DefaultSubjectDAO();
    DefaultSessionStorageEvaluator sessionStorageEvaluator = new DefaultSessionStorageEvaluator();
    sessionStorageEvaluator.setSessionStorageEnabled(false);

    subjectDAO.setSessionStorageEvaluator(sessionStorageEvaluator);
    setSubjectDAO(subjectDAO);
  }
}
