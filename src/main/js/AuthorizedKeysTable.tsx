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
import { AuthorizedKey } from "./types";
import AuthorizedKeyRow from "./AuthorizedKeyRow";

type Props = {
  onKeyDeleted: (error?: Error) => void;
  authorizedKeys: AuthorizedKey[];
};

const AuthorizedKeysTable: FC<Props> = ({ onKeyDeleted, authorizedKeys }) => {
  const [t] = useTranslation("plugins");

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
        {authorizedKeys.map((authorizedKey, index) => {
          return <AuthorizedKeyRow key={index} onKeyDeleted={onKeyDeleted} authorizedKey={authorizedKey} />;
        })}
      </tbody>
    </table>
  );
};

export default AuthorizedKeysTable;
