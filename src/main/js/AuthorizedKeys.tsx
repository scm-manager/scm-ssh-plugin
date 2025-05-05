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
import { useTranslation } from "react-i18next";
import { Loading, ErrorNotification, Subtitle, useDocumentTitle } from "@scm-manager/ui-core";
import { Link, Me, User } from "@scm-manager/ui-types";
import { useAuthorizedKeys, useDeleteAuthorizedKey } from "./authorizedKeys";
import AuthorizedKeysTable from "./AuthorizedKeysTable";
import AddAuthorizedKey from "./AddAuthorizedKey";

type Props = {
  user: User | Me;
};

const AuthorizedKeys: React.FC<Props> = ({ user }) => {
  const [t] = useTranslation("plugins");
  useDocumentTitle(t("scm-ssh-plugin.authorizedKeys.title"), user.displayName);
  const { isLoading, data: authorizedKeys, error: fetchError } = useAuthorizedKeys(user);
  const { error: deletionError, remove } = useDeleteAuthorizedKey(user);
  const error = deletionError || fetchError;

  const createLink = (authorizedKeys?._links?.create as Link)?.href;

  if (error) {
    return <ErrorNotification error={error} />;
  }

  if (!authorizedKeys || isLoading) {
    return <Loading />;
  }

  return (
    <>
      <Subtitle>{t("scm-ssh-plugin.authorizedKeys.title")}</Subtitle>
      <p>{t("scm-ssh-plugin.authorizedKeys.description")}</p>
      <br />
      <AuthorizedKeysTable authorizedKeys={authorizedKeys} onDelete={remove} />
      {createLink && <AddAuthorizedKey user={user} authorizedKeys={authorizedKeys} />}
    </>
  );
};

export default AuthorizedKeys;
