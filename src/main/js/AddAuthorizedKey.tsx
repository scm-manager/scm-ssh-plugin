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

import React, { FC, useState } from "react";
import { useTranslation } from "react-i18next";
import { InputField, SubmitButton, Textarea } from "@scm-manager/ui-components";
import { ErrorNotification, Level, Subtitle } from "@scm-manager/ui-core";
import { Me, User } from "@scm-manager/ui-types";
import { AuthorizedKeysCollection } from "./types";
import { useCreateAuthorizedKey } from "./authorizedKeys";

type Props = {
  user: User | Me;
  authorizedKeys: AuthorizedKeysCollection;
};

const AddAuthorizedKey: FC<Props> = ({ user, authorizedKeys }) => {
  const [t] = useTranslation("plugins");
  const {
    isLoading,
    error,
    create,
  } = useCreateAuthorizedKey(user, authorizedKeys);
  const [displayName, setDisplayName] = useState("");
  const [raw, setRaw] = useState("");

  const isValid = () => {
    return !!displayName && !!raw;
  };

  const resetForm = () => {
    setDisplayName("");
    setRaw("");
  };

  const addKey = () => {
    create({ raw, displayName });
    resetForm();
  };

  return (
    <>
      <hr />
      {error ? <ErrorNotification error={error} /> : null}
      <Subtitle>{t("scm-ssh-plugin.authorizedKeys.addSubtitle")}</Subtitle>
      <InputField
        label={t("scm-ssh-plugin.authorizedKeys.displayName")}
        value={displayName}
        onChange={setDisplayName}
      />
      <Textarea name="raw" label={t("scm-ssh-plugin.authorizedKeys.raw")} value={raw} onChange={setRaw} />
      <Level
        right={
          <SubmitButton
            label={t("scm-ssh-plugin.authorizedKeys.addKey")}
            loading={isLoading}
            disabled={!isValid() || isLoading}
            action={addKey}
          />
        }
      />
    </>
  );
};

export default AddAuthorizedKey;
