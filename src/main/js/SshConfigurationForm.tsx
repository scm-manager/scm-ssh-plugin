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
import React from "react";
import { Checkbox, InputField, Notification, Select } from "@scm-manager/ui-components";
import { WithTranslation, withTranslation } from "react-i18next";
import { validateHostnameWithPort, validatePort } from "./validator";

type SshConfiguration = {
  hostName?: string;
  port: number;
  disablePasswordAuthentication: boolean;
  algorithm: string;
};

type Props = WithTranslation & {
  initialConfiguration: SshConfiguration;
  readOnly: boolean;
  onConfigurationChange: (configuration: SshConfiguration, valid: boolean) => void;
};

type State = SshConfiguration & {
  hostNameValidationError?: boolean;
  portValidationError?: boolean;
};

class SshConfigurationForm extends React.Component<Props, State> {
  constructor(props: Props) {
    super(props);
    this.state = {
      ...props.initialConfiguration
    };
  }

  onAlgorithmChanged = (value: string) => {
    this.setState(
      {
        algorithm: value
      },
      this.onStateChange
    );
  };

  onPortChanged = (value: string) => {
    let portValidationError = !validatePort(value);
    this.setState(
      {
        port: parseInt(value),
        portValidationError
      },
      this.onStateChange
    );
  };

  onHostnameChanged = (value: string) => {
    let hostNameValidationError = !validateHostnameWithPort(value);
    this.setState(
      {
        hostName: value,
        hostNameValidationError
      },
      this.onStateChange
    );
  };

  onDisablePasswordAuthenticationChanged = () => {
    this.setState(
      prevState => ({
        disablePasswordAuthentication: !prevState.disablePasswordAuthentication
      }),
      this.onStateChange
    );
  };

  onStateChange = () => {
    const { hostName, port, disablePasswordAuthentication, algorithm } = this.state;
    this.props.onConfigurationChange({ hostName, port, disablePasswordAuthentication, algorithm }, this.isValid());
  };

  isValid = () => {
    const { hostNameValidationError, portValidationError } = this.state;
    return !hostNameValidationError && !portValidationError;
  };

  render(): React.ReactNode {
    const { t } = this.props;
    const {
      hostName,
      hostNameValidationError,
      port,
      portValidationError,
      disablePasswordAuthentication,
      algorithm
    } = this.state;
    return (
      <>
        <InputField
          onChange={this.onHostnameChanged}
          name="hostName"
          label={t("scm-ssh-plugin.globalConfig.hostName")}
          helpText={t("scm-ssh-plugin.globalConfig.hostNameHelp")}
          errorMessage={t("scm-ssh-plugin.globalConfig.hostNameInvalid")}
          validationError={hostNameValidationError}
          value={hostName}
        />
        <InputField
          onChange={this.onPortChanged}
          name="port"
          label={t("scm-ssh-plugin.globalConfig.port")}
          helpText={t("scm-ssh-plugin.globalConfig.portHelp")}
          errorMessage={t("scm-ssh-plugin.globalConfig.portInvalid")}
          validationError={portValidationError}
          value={"" + port}
          type="number"
        />
        <Checkbox
          onChange={this.onDisablePasswordAuthenticationChanged}
          label={t("scm-ssh-plugin.globalConfig.disablePasswordAuthentication")}
          helpText={t("scm-ssh-plugin.globalConfig.disablePasswordAuthenticationHelp")}
          checked={disablePasswordAuthentication}
        />
        <Notification type="warning">{t("scm-ssh-plugin.globalConfig.hostKeyAlgorithmNotification")}</Notification>
        <Select
          options={[
            { value: "RSA", label: "RSA" },
            { value: "EC", label: "EC" }
          ]}
          onChange={this.onAlgorithmChanged}
          name="hostKeyAlgorithm"
          label={t("scm-ssh-plugin.globalConfig.hostKeyAlgorithm")}
          helpText={t("scm-ssh-plugin.globalConfig.hostKeyAlgorithmHelp")}
          value={algorithm}
        />
      </>
    );
  }
}

export default withTranslation("plugins")(SshConfigurationForm);
