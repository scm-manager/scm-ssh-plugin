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
import { AuthorizedKey, AuthorizedKeysCollection } from "./types";
import { Notification } from "@scm-manager/ui-core";
import { DeleteFunction } from "@scm-manager/ui-api";
import AuthorizedKeyEntry from "./AuthorizedKeyEntry";

type Props = {
  authorizedKeys?: AuthorizedKeysCollection;
  onDelete: DeleteFunction<AuthorizedKey>;
};

const AuthorizedKeysTable: FC<Props> = ({ authorizedKeys, onDelete }) => {
  const [t] = useTranslation("plugins");

  if (authorizedKeys?._embedded?.keys?.length === 0) {
    return <Notification type="info">{t("scm-ssh-plugin.authorizedKeys.noStoredKeys")}</Notification>;
  }

  return (
    <table className="card-table table is-hoverable is-fullwidth">
      <thead>
        <tr>
          <th>{t("scm-ssh-plugin.authorizedKeys.displayName")}</th>
          <th>{t("scm-ssh-plugin.authorizedKeys.created")}</th>
          <th className="is-hidden-mobile">{t("scm-ssh-plugin.authorizedKeys.raw")}</th>
          <th />
        </tr>
      </thead>
      <tbody>
        {authorizedKeys?._embedded?.keys?.map((authorizedKey: AuthorizedKey, index: number) => {
          return <AuthorizedKeyEntry key={index} onDelete={onDelete} authorizedKey={authorizedKey} />;
        })}
      </tbody>
    </table>
  );
};

export default AuthorizedKeysTable;
