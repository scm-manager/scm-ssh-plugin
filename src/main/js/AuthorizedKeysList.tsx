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
import { useTranslation } from "react-i18next";
import { AuthorizedKeysCollection } from "./types";
import { Notification } from "@scm-manager/ui-components";
import AuthorizedKeysTable from "./AuthorizedKeysTable";

type Props = {
  authorizedKeys?: AuthorizedKeysCollection;
  onKeyDeleted: (error?: Error) => void;
};

const AuthorizedKeysList: FC<Props> = ({ authorizedKeys, onKeyDeleted }) => {
  const [t] = useTranslation("plugins");

  if (!authorizedKeys || authorizedKeys._embedded.keys.length <= 0) {
    return <Notification type="info">{t("scm-ssh-plugin.authorizedKeys.noStoredKeys")}</Notification>;
  }

  return <AuthorizedKeysTable authorizedKeys={authorizedKeys._embedded.keys} onKeyDeleted={onKeyDeleted} />;
};

export default AuthorizedKeysList;
