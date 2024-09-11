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
