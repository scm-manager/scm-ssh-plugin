// @flow
import React from "react";
import { Title, Configuration } from "@scm-manager/ui-components";
import { translate } from "react-i18next";
import SshConfigurationForm from "./SshConfigurationForm";

type Props = {
  link: string,
  t: string => string
};

class LdapConfiguration extends React.Component<Props> {
  render(): React.ReactNode {
    const { t, link } = this.props;
    return (
      <>
        <Title title={t("scm-ssh-plugin.globalConfig.header")} />
        <Configuration
          link={link}
          t={t}
          render={props => <SshConfigurationForm {...props} />}
        />
      </>
    );
  }
}

export default translate("plugins")(LdapConfiguration);
