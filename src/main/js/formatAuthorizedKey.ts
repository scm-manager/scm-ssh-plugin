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

export function formatAuthorizedKey(key: string) {
  const parts = key.split(/\s+/);
  if (parts.length === 3) {
    return parts[0] + " ... " + parts[2];
  } else if (parts.length === 2) {
    if (parts[0].length >= parts[1].length) {
      return parts[0].substring(0, 7) + "... " + parts[1];
    } else {
      const keyLength = parts[1].length;
      return parts[0] + " ..." + parts[1].substring(keyLength - 7);
    }
  } else {
    const keyLength = parts[0].length;
    if (keyLength < 15) {
      return parts[0];
    }
    return parts[0].substring(0, 7) + "..." + parts[0].substring(keyLength - 7);
  }
}
