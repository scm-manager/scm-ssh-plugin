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
