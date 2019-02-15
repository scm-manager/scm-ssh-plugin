// @flow
import { binder } from "@scm-manager/ui-extensions";

import NavigationLink from "./NavigationLink";
import NavigationRoute from "./NavigationRoute";

const predicate = props => {
  return props.me && props.me._links && props.me._links.authorized_keys;
};

binder.bind("profile.subnavigation", NavigationLink, predicate);
binder.bind("profile.route", NavigationRoute, predicate);
