//@flow
import React from "react";
import { translate } from "react-i18next";
import { NavLink } from "@scm-manager/ui-components";

type Props = {
  url: string,
  // context props
  t: string => string
};

class NavigationLink extends React.Component<Props> {
  render() {
    const { url, t } = this.props;
    return (
      <NavLink
        to={`${url}/settings/authorized_keys`}
        label={t("scm-ssh-plugin.navigation")}
      />
    );
  }
}

export default translate("plugins")(NavigationLink);
