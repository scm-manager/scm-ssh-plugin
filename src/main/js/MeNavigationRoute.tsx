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

import React, { FC } from "react";
import { Route, Switch } from "react-router-dom";
import { Link, Me } from "@scm-manager/ui-types";
import MeConfiguration from "./MeConfiguration";
import AuthorizedKeys from "./AuthorizedKeys";
import { useSubject } from "@scm-manager/ui-api";

type Props = {
  url: string;
  me: Me;
};

const MeNavigationRoute: FC<Props> = ({ url, me }) => {
  const { isAnonymous } = useSubject();

  return (
    <Switch>
      {!isAnonymous && (
        <Route path={`${url}/settings/sshPreferences`}>
          <MeConfiguration link={(me?._links?.sshPreferences as Link)?.href} />
        </Route>
      )}
      <Route path={`${url}/settings/authorized_keys`}>
        <AuthorizedKeys user={me} />
      </Route>
    </Switch>
  );
};

export default MeNavigationRoute;
