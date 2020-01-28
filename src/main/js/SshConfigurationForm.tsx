import React from "react";
import { Checkbox, InputField } from "@scm-manager/ui-components";
import { WithTranslation, withTranslation } from "react-i18next";
import { validateHostnameWithPort, validatePort } from "./validator";

type SshConfiguration = {
  hostName?: string;
  port: number;
  disablePasswordAuthentication: boolean;
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
    const { hostName, port, disablePasswordAuthentication } = this.state;
    this.props.onConfigurationChange({ hostName, port, disablePasswordAuthentication }, this.isValid());
  };

  isValid = () => {
    const { hostNameValidationError, portValidationError } = this.state;
    return !hostNameValidationError && !portValidationError;
  };

  render(): React.ReactNode {
    const { t } = this.props;
    const { hostName, hostNameValidationError, port, portValidationError, disablePasswordAuthentication } = this.state;
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
      </>
    );
  }
}

export default withTranslation("plugins")(SshConfigurationForm);
