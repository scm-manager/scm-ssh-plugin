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
    return (
      <>
        <InputField
          onChange={this.valueChangeHandler}
          name="hostName"
          label={this.props.t("scm-ssh-plugin.globalConfig.hostName")}
          helpText={this.props.t("scm-ssh-plugin.globalConfig.hostNameHelp")}
          value={this.state.hostName}/>
        <InputField
          onChange={this.valueChangeHandler}
          name="port"
          label={this.props.t("scm-ssh-plugin.globalConfig.port")}
          helpText={this.props.t("scm-ssh-plugin.globalConfig.portHelp")}
          value={this.state.port} type="number"/>
      </>
      );
  }
}

export default translate("plugins")(SshConfigurationForm);
