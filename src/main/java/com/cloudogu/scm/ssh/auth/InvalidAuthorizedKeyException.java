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
