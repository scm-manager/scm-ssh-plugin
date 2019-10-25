import { Collection, Links } from "@scm-manager/ui-types";

export type AuthorizedKeysCollection = Collection & {
  _embedded: {
    keys: AuthorizedKey[];
  };
};

export type AuthorizedKey = {
  displayName: string;
  raw: string;
  created?: string;
  _links: Links;
};
