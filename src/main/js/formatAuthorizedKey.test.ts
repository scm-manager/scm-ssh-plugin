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

import { formatAuthorizedKey } from "./formatAuthorizedKey";

describe("format authorized key tests", () => {
  it("should format the given key", () => {
    const key = "ssh-rsa ACB0DEFGHIJKLMOPQRSTUVWXYZ tricia@hitchhiker.com";
    expect(formatAuthorizedKey(key)).toEqual("ssh-rsa ... tricia@hitchhiker.com");
  });

  it("should use the first chars of the key without prefix", () => {
    const key = "ACB0DEFGHIJKLMOPQRSTUVWXYZ tricia@hitchhiker.com";
    expect(formatAuthorizedKey(key)).toEqual("ACB0DEF... tricia@hitchhiker.com");
  });

  it("should use the last chars of the key without suffix", () => {
    const key = "ssh-rsa ACB0DEFGHIJKLMOPQRSTUVWXYZ";
    expect(formatAuthorizedKey(key)).toEqual("ssh-rsa ...TUVWXYZ");
  });

  it("should use a few chars from the beginning and a few from the end, if the key has no prefix and suffix", () => {
    const key = "ACB0DEFGHIJKLMOPQRSTUVWXYZ0123456789";
    expect(formatAuthorizedKey(key)).toEqual("ACB0DEF...3456789");
  });

  it("should return the whole string for a short key", () => {
    const key = "ABCDE";
    expect(formatAuthorizedKey(key)).toEqual("ABCDE");
  });
});
