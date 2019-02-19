package com.cloudogu.scm.ssh.auth;

/**
 * Exception is thrown if an unsupported or invalid key was added to the store.
 */
class InvalidAuthorizedKeyException extends RuntimeException {

  InvalidAuthorizedKeyException(String message, Throwable cause) {
    super(message, cause);
  }
}
