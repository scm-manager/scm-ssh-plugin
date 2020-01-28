import React from "react";
import { WithTranslation, withTranslation } from "react-i18next";
import { Title, Configuration } from "@scm-manager/ui-components";
import SshConfigurationForm from "./SshConfigurationForm";

type Props = WithTranslation & {
  link: string;
};

class LdapConfiguration extends React.Component<Props> {
  render() {
    const { t, link } = this.props;
    return (
      <>
        <Title title={t("scm-ssh-plugin.globalConfig.title")} />
        <Configuration link={link} render={props => <SshConfigurationForm {...props} />} />
      </>
    );
  }
}

export default withTranslation("plugins")(LdapConfiguration);
