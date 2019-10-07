//@flow
import React from "react";
import {Configuration, InputField} from "@scm-manager/ui-components";
import {translate} from "react-i18next";

type SshConfiguration = {
  hostName: string,
  port: number
}

type Props = {
  initialConfiguration: Configuration,
  readOnly: boolean,
  onConfigurationChange: (Configuration, boolean) => void,
  t: string => string
};

type State = SshConfiguration;

class SshConfigurationForm extends React.Component<Props, State> {
  constructor(props: Props) {
    super(props);
    console.log("initial config:", props.initialConfiguration);
    this.state = {...props.initialConfiguration}
  }

  valueChangeHandler = (value: any, name: string) => {
    this.setState(
      {
        [name]: value
      },
      () => this.props.onConfigurationChange({...this.state}, true)
    );
  };

  render(): React.ReactNode {
    const {t} = this.props;
    const {hostName, port} = this.state;
    const hostNameValid = true;
    const portValid = port > -2 && port < 0xC000;
    return (
      <>
        <InputField
          onChange={this.valueChangeHandler}
          name="hostName"
          label={t("scm-ssh-plugin.globalConfig.hostName")}
          helpText={t("scm-ssh-plugin.globalConfig.hostNameHelp")}
          errorMessage={t("scm-ssh-plugin.globalConfig.hostNameInvalid")}
          validationError={!hostNameValid}
          value={hostName}/>
        <InputField
          onChange={this.valueChangeHandler}
          name="port"
          label={t("scm-ssh-plugin.globalConfig.port")}
          helpText={t("scm-ssh-plugin.globalConfig.portHelp")}
          errorMessage={t("scm-ssh-plugin.globalConfig.portInvalid")}
          validationError={!portValid}
          value={port} type="number"/>
      </>
      );
  }
}

export default translate("plugins")(SshConfigurationForm);
