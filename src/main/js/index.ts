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

import { binder } from "@scm-manager/ui-extensions";
import { ConfigurationBinder as cfgBinder } from "@scm-manager/ui-components";
import MeNavigationLink from "./MeNavigationLink";
import MeNavigationRoute from "./MeNavigationRoute";
import UserNavigationLink from "./UserNavigationLink";
import UserNavigationRoute from "./UserNavigationRoute";
import SshConfiguration from "./SshConfiguration";

// me page

const mePredicate = props => {
  return props.me && props.me._links && (props.me._links.authorized_keys || props.me._links.sshPreferences);
};

binder.bind("profile.setting", MeNavigationLink, mePredicate);
binder.bind("profile.route", MeNavigationRoute, mePredicate);

// user page

const userPredicate = props => {
  return props.user && props.user._links && props.user._links.authorized_keys;
};

binder.bind("user.setting", UserNavigationLink, userPredicate);
binder.bind("user.route", UserNavigationRoute, userPredicate);

// global configuration

cfgBinder.bindGlobal("/ssh", "scm-ssh-plugin.globalConfig.navLink", "sshConfig", SshConfiguration);
