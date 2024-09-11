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

const portPattern = /^[0-9]+$/;
const minPort = 1;
const maxPort = 65535;

export function validatePort(value?: string) {
  if (value && portPattern.test(value)) {
    const port = parseInt(value);
    return port >= minPort && port <= maxPort;
  }
  return false;
}

const sshHostnameWithPortPattern = /^(ssh:\/\/)?[^: \/]+(:[0-9]+)?$/;

export function validateHostnameWithPort(value?: string) {
  if (!value) {
    return true;
  }
  return sshHostnameWithPortPattern.test(value);
}
