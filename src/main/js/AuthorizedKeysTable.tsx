import React from "react";
import { WithTranslation, withTranslation } from "react-i18next";
import { AuthorizedKey } from "./types";
import AuthorizedKeyRow from "./AuthorizedKeyRow";

type Props = WithTranslation & {
  onKeyDeleted: (error?: Error) => void;
  authorizedKeys: AuthorizedKey[];
};

class AuthorizedKeysTable extends React.Component<Props> {
  render() {
    const { authorizedKeys, onKeyDeleted, t } = this.props;
    return (
      <table className="card-table table is-hoverable is-fullwidth">
        <thead>
          <tr>
            <th>{t("scm-ssh-plugin.displayName")}</th>
            <th>{t("scm-ssh-plugin.created")}</th>
            <th className="is-hidden-mobile">{t("scm-ssh-plugin.raw")}</th>
            <th />
          </tr>
        </thead>
        <tbody>
          {authorizedKeys.map((authorizedKey, index) => {
            return <AuthorizedKeyRow key={index} onKeyDeleted={onKeyDeleted} authorizedKey={authorizedKey} />;
          })}
        </tbody>
      </table>
    );
  }
}

export default withTranslation("plugins")(AuthorizedKeysTable);