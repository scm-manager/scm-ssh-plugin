//@flow
import type { Links } from "@scm-manager/ui-types";

export type AuthorizedKey = {
  displayName: string,
  raw: string,
  created?: string,
  _links: Links
};
