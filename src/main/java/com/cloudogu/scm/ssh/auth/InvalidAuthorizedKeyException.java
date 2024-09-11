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

import sonia.scm.BadRequestException;

import static java.util.Collections.emptyList;

/**
 * Exception is thrown if an unsupported or invalid key was added to the store.
 */
class InvalidAuthorizedKeyException extends BadRequestException {

  public static final String ERROR_CODE = "3fRby4izZ1";

  InvalidAuthorizedKeyException(Exception cause) {
    super(emptyList(), "invalid or unsupported key", cause);
  }

  @Override
  public String getCode() {
    return ERROR_CODE;
  }
}
