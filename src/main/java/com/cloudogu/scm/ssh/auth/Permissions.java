package com.cloudogu.scm.ssh.auth;

final class Permissions {

  private static final String ACTION_READ = "readAuthorizedKeys";
  private static final String ACTION_WRITE = "writeAuthorizedKeys";

  private Permissions() {
  }

  static String readAndWriteAuthorizedKeys(String user) {
    return String.format("user:%s,%s:%s", ACTION_READ, ACTION_WRITE, user);
  }

  static String readAuthorizedKeys(String user) {
    return String.format("user:%s:%s", ACTION_READ, user);
  }

  static String writeAuthorizedKeys(String user) {
    return String.format("user:%s:%s", ACTION_WRITE, user);
  }
}
