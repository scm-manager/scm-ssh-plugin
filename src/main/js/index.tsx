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

import React from "react";
import { useMeLink, useUserLink } from "@scm-manager/ui-api";
import { ConfigurationBinder as cfgBinder } from "@scm-manager/ui-components";
import { binder } from "@scm-manager/ui-extensions";
import { Me, User } from "@scm-manager/ui-types";
import { MeSshPreferenceRouteElement, MeAuthorizedKeysRouteElement } from "./MeNavigationRoute";
import { UserAuthorizedKeysRouteElement } from "./UserNavigationRoute";
import MeNavigationLink from "./MeNavigationLink";
import UserNavigationLink from "./UserNavigationLink";
import SshConfiguration from "./SshConfiguration";

// me page

const mePredicate = () => {
  return useMeLink("authorized_keys") || useMeLink("sshPreferences");
};

binder.bind("profile.setting", MeNavigationLink, mePredicate);

const meRoute = (props: { me: Me }) => ({
  children: [
    {
      path: "sshPreferences",
      element: <MeSshPreferenceRouteElement />,
    },
    {
      path: "authorized_keys",
      element: <MeAuthorizedKeysRouteElement />,
    },
  ],
});

binder.bind("profile.route", meRoute);

// user page

const userPredicate = (props: { user: User }) => {
  return useUserLink(props.user.name, "authorized_keys");
};

binder.bind("user.setting", UserNavigationLink, userPredicate);

const userRoute = (props: { user: User }) => ({
  path: "authorized_keys",
  element: <UserAuthorizedKeysRouteElement user={props.user} />,
});

binder.bind("user.route", userRoute);

// global configuration

cfgBinder.bindAdminSetting("/ssh", "scm-ssh-plugin.globalConfig.navLink", "sshConfig", SshConfiguration);
