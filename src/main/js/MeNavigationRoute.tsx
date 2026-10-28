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
import { useMe, useSubject } from "@scm-manager/ui-api";
import { Link } from "@scm-manager/ui-types";
import MeConfiguration from "./MeConfiguration";
import AuthorizedKeys from "./AuthorizedKeys";

export const MeSshPreferenceRouteElement = () => {
  const { data: me } = useMe();
  const { isAnonymous } = useSubject();

  if (!me || isAnonymous) {
    return null;
  }

  return <MeConfiguration link={(me._links?.sshPreferences as Link)?.href} />;
};

export const MeAuthorizedKeysRouteElement = () => {
  const { data: me } = useMe();

  if (!me) {
    return null;
  }

  return <AuthorizedKeys user={me} />;
};
