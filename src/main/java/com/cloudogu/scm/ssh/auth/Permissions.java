package com.cloudogu.scm.ssh.auth;

import sonia.scm.user.UserPermissions;

final class Permissions {

  private static final String ACTION_READ = "readAuthorizedKeys";
  private static final String ACTION_WRITE = "writeAuthorizedKeys";

  private Permissions() {
  }

  static String readAndWriteAuthorizedKeys(String user) {
    return String.format("user:%s,%s:%s", ACTION_READ, ACTION_WRITE, user);
  }

  static void checkReadAuthorizedKeys(String user) {
    UserPermissions.custom(ACTION_READ, user).check();
  }

  static boolean isPermittedReadAuthorizedKeys(String user) {
    return UserPermissions.custom(ACTION_READ, user).isPermitted();
  }

  static void checkWriteAuthorizedKeys(String user) {
    UserPermissions.custom(ACTION_WRITE, user).check();
  }

  static boolean isPermittedWriteAuthorizedKeys(String user) {
    return UserPermissions.custom(ACTION_WRITE, user).isPermitted();
  }
}
