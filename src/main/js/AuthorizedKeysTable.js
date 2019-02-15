//@flow
import React from "react";
import { translate } from "react-i18next";
import type { AuthorizedKey } from "./types";
import AuthorizedKeyRow from "./AuthorizedKeyRow";

type Props = {
  onDelete: AuthorizedKey => void,
  authorizedKeys: AuthorizedKey[],
  // context props
  t: string => string
};

class AuthorizedKeysTable extends React.Component<Props> {
  render() {
    const { authorizedKeys, onDelete, t } = this.props;
    return (
      <table className="card-table table is-hoverable is-fullwidth">
        <thead>
          <tr>
            <th>{t("scm-ssh-plugin.displayName")}</th>
            <th>{t("scm-ssh-plugin.created")}</th>
            <th className="is-hidden-mobile">
              {t("scm-ssh-plugin.authorizedKey")}
            </th>
            <th />
          </tr>
        </thead>
        <tbody>
          {authorizedKeys.map((authorizedKey, index) => {
            return (
              <AuthorizedKeyRow
                key={index}
                onDelete={onDelete}
                authorizedKey={authorizedKey}
              />
            );
          })}
        </tbody>
      </table>
    );
  }
}

export default translate("plugins")(AuthorizedKeysTable);
