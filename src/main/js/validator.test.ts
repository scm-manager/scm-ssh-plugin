import {validateHostnameWithPort, validatePort} from "./validator";

describe("test validatePort", () => {

  it("should return false for undefined value", () => {
    expect(validatePort()).toBe(false);
  });

  it("should return false for empty string", () => {
    expect(validatePort("")).toBe(false);
  });

  it("should return false for invalid ports", () => {
    const invalid = [
      "a",
      "av",
      "12.34",
      "1 2 3",
      "1.2",
      "666666666",
      "-12"
    ];

    for (const v of invalid) {
      expect(validatePort(v)).toBe(false);
    }
  });

  it("should return true for valid hostnames", () => {
    const valid = [
      "22",
      "2222",
      "22222"
    ];

    for (const v of valid) {
      expect(validatePort(v)).toBe(true);
    }
  });

});

describe("test validateHostnameWithPort", () => {

  it("should return true for undefined value", () => {
    expect(validateHostnameWithPort()).toBe(true);
  });

  it("should return true for empty string", () => {
    expect(validateHostnameWithPort("")).toBe(true);
  });

  it("should return false for invalid hostnames", () => {
    const invalid = [
      "http://ssh.cloudogu.com",
      "ftp://ssh.cloudogu.com",
      "ssh/cloudogu/com",
      "ssh.cloudogu.com:4a2",
      "ssh cloudogu com",
      "ssh:cloudogu:com"
    ];

    for (const v of invalid) {
      expect(validateHostnameWithPort(v)).toBe(false);
    }
  });

  it("should return true for valid hostnames", () => {
    const valid = [
    "ssh://ssh.cloudogu.com",
      "ssh://ssh.cloudogu.com:22",
      "ssh://ssh.cloudogu.com:2222",
      "192.168.22.22:2222",
      "192.168.22.22"
    ];

    for (const v of valid) {
      expect(validateHostnameWithPort(v)).toBe(true);
    }
  });
});
