/*
 * MIT License
 *
 * Copyright (c) 2020-present Cloudogu GmbH and Contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
import React, { FC } from "react";
import { useTranslation } from "react-i18next";
import { Notification, Title } from "@scm-manager/ui-components";
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

  return (
    <>
      <Title title={t("scm-ssh-plugin.globalConfig.title")} />
      <ConfigurationForm<SshConfiguration> link={link} translationPath={["plugins", "scm-ssh-plugin.globalConfig"]}>
        <Form.Input
          name="hostName"
          rules={{
            validate: validator.validateHostnameWithPort,
            required: true
          }}
        />
        <Form.Input
          name="port"
          rules={{
            validate: validator.validatePort
          }}
          type="number"
        />
        <Form.Checkbox name="disablePasswordAuthentication" />
        <Notification type="warning">{t("scm-ssh-plugin.globalConfig.hostKeyAlgorithm.notification")}</Notification>
        <Form.Select
          name="algorithm"
          options={[
            { label: "RSA", value: "RSA" },
            { label: "EC", value: "EC" }
          ]}
        />
      </ConfigurationForm>
    </>
  );
};

export default SshConfiguration;
