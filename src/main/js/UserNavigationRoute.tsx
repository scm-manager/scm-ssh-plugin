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
import { Route } from "react-router-dom";
import { Link, User } from "@scm-manager/ui-types";
import AuthorizedKeys from "./AuthorizedKeysPage";

type Props = {
  url: string;
  user: User;
};

const UserNavigationRoute: FC<Props> = ({ url, user }) => {
  return (
    <Route path={`${url}/settings/authorized_keys`}>
      <AuthorizedKeys link={(user?._links?.authorized_keys as Link)?.href} />
    </Route>
  );
};

export default UserNavigationRoute;
