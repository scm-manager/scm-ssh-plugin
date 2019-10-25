import React from "react";
import { WithTranslation, withTranslation } from "react-i18next";
import { Title, Configuration } from "@scm-manager/ui-components";
import SshConfigurationForm from "./SshConfigurationForm";

type Props = {
  link: string;
  t: (p: string) => string;
};

class LdapConfiguration extends React.Component<Props> {
  render() {
    const { t, link } = this.props;
    return (
      <>
        <Title title={t("scm-ssh-plugin.globalConfig.header")} />
        <Configuration link={link} t={t} render={props => <SshConfigurationForm {...props} />} />
      </>
    );
  }
}

export default withTranslation("plugins")(LdapConfiguration);
