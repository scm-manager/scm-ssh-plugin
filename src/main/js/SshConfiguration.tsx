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
import { Notification, Title, useDocumentTitle } from "@scm-manager/ui-core";
import { ConfigurationForm, Form } from "@scm-manager/ui-forms";
import * as validator from "./validator";

type SshConfiguration = {
  hostName?: string;
  port: number;
  disablePasswordAuthentication: boolean;
  algorithm: string;
};

const SshConfiguration: FC<{ link: string }> = ({ link }) => {
  const [t] = useTranslation("plugins");
  useDocumentTitle(t("scm-ssh-plugin.globalConfig.title"));

  return (
    <>
      <Title title={t("scm-ssh-plugin.globalConfig.title")} />
      <ConfigurationForm<SshConfiguration> link={link} translationPath={["plugins", "scm-ssh-plugin.globalConfig"]}>
        <Form.Input
          name="hostName"
          rules={{
            validate: validator.validateHostnameWithPort,
            required: true,
          }}
        />
        <Form.Input
          name="port"
          rules={{
            validate: validator.validatePort,
          }}
          type="number"
        />
        <Form.Checkbox name="disablePasswordAuthentication" />
        <Notification type="warning">{t("scm-ssh-plugin.globalConfig.hostKeyAlgorithm.notification")}</Notification>
        <Form.Select
          name="algorithm"
          options={[
            { label: "RSA", value: "RSA" },
            { label: "EC", value: "EC" },
          ]}
        />
      </ConfigurationForm>
    </>
  );
};

export default SshConfiguration;
