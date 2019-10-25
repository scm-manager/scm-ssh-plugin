import React from "react";
import { WithTranslation, withTranslation } from "react-i18next";
import { AuthorizedKeysCollection } from "./types";
import { Notification } from "@scm-manager/ui-components";
import AuthorizedKeysTable from "./AuthorizedKeysTable";

type Props = WithTranslation & {
  authorizedKeys?: AuthorizedKeysCollection;
  onKeyDeleted: (error?: Error) => void;
};

class AuthorizedKeysList extends React.Component<Props> {
  render() {
    const { authorizedKeys, t } = this.props;

    if (!authorizedKeys || authorizedKeys._embedded.keys.length <= 0) {
      return <Notification type="info">{t("scm-ssh-plugin.noStoredKeys")}</Notification>;
    }

    const { onKeyDeleted } = this.props;
    return <AuthorizedKeysTable authorizedKeys={authorizedKeys._embedded.keys} onKeyDeleted={onKeyDeleted} />;
  }
}

export default withTranslation("plugins")(AuthorizedKeysList);
