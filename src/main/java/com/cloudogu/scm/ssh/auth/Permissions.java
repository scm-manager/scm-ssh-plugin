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
