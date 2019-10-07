// @flow
import { binder } from "@scm-manager/ui-extensions";
import { ConfigurationBinder as cfgBinder } from "@scm-manager/ui-components";
import NavigationLink from "./NavigationLink";
import MeNavigationRoute from "./MeNavigationRoute";
import UserNavigationRoute from "./UserNavigationRoute";
import SshConfiguration from "./SshConfiguration";

// me page

const mePredicate = props => {
  return props.me && props.me._links && props.me._links.authorized_keys;
};

binder.bind("profile.setting", NavigationLink, mePredicate);
binder.bind("profile.route", MeNavigationRoute, mePredicate);

// user page

const userPredicate = props => {
  return props.user && props.user._links && props.user._links.authorized_keys;
};

binder.bind("user.setting", NavigationLink, userPredicate);
binder.bind("user.route", UserNavigationRoute, userPredicate);

// global configuration

cfgBinder.bindGlobal(
  "/ssh",
  "scm-ssh-plugin.globalConfig.navLink",
  "sshConfig",
  SshConfiguration
);
