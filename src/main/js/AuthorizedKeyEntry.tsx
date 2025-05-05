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
import { DeleteFunction } from "@scm-manager/ui-api";
import { Button, DateFromNow } from "@scm-manager/ui-components";
import { AuthorizedKey } from "./types";
import { formatAuthorizedKey } from "./formatAuthorizedKey";

type Props = {
  authorizedKey: AuthorizedKey;
  onDelete: DeleteFunction<AuthorizedKey>;
};

export const AuthorizedKeyEntry: FC<Props> = ({ authorizedKey, onDelete }) => {
  const [t] = useTranslation("users");

  let deleteButton;
  if (authorizedKey?._links?.delete) {
    deleteButton = (
      <Button
        color="text"
        icon="trash"
        action={() => onDelete(authorizedKey)}
        title={t("authorizedKey.delete")}
        className="px-2"
      />
    );
  }

  return (
    <tr>
      <td className="is-vertical-align-middle">{authorizedKey.displayName}</td>
      <td className="is-vertical-align-middle is-hidden-mobile">
        <DateFromNow date={authorizedKey.created} />
      </td>
      <td className="is-vertical-align-middle has-text-centered is-hidden-mobile">
        {formatAuthorizedKey(authorizedKey.raw)}
      </td>
      <td className="is-vertical-align-middle has-text-centered">{deleteButton}</td>
    </tr>
  );
};

export default AuthorizedKeyEntry;
