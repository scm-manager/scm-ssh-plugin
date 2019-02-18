// @flow
import { binder } from "@scm-manager/ui-extensions";

import NavigationLink from "./NavigationLink";
import MeNavigationRoute from "./MeNavigationRoute";
import UserNavigationRoute from "./UserNavigationRoute";

// me page

const mePredicate = props => {
  return props.me && props.me._links && props.me._links.authorized_keys;
};

binder.bind("profile.subnavigation", NavigationLink, mePredicate);
binder.bind("profile.route", MeNavigationRoute, mePredicate);

// user page

const userPredicate = props => {
  return props.user && props.user._links && props.user._links.authorized_keys;
};

binder.bind("user.subnavigation", NavigationLink, userPredicate);
binder.bind("user.route", UserNavigationRoute, userPredicate);

