import React from "react";
import { WithTranslation, withTranslation } from "react-i18next";
import { AuthorizedKey } from "./types";
import { apiClient, DateFromNow, DeleteButton } from "@scm-manager/ui-components";
import { formatAuthorizedKey } from "./formatAuthorizedKey";

type Props = WithTranslation & {
  onKeyDeleted: (error?: Error) => void;
  authorizedKey: AuthorizedKey;
};

type State = {
  loading: boolean;
};

class AuthorizedKeyRow extends React.Component<Props, State> {
  constructor(props: Props) {
    super(props);
    this.state = {
      loading: false
    };
  }

  onDelete = (url: string) => {
    this.setState({
      loading: true
    });

    const { onKeyDeleted } = this.props;

    apiClient
      .delete(url)
      .then(() => {
        this.setState({
          loading: false
        });

        onKeyDeleted();
      })
      .catch(error => {
        this.setState({
          loading: false
        });

        onKeyDeleted(error);
      });
  };

  render() {
    const { authorizedKey } = this.props;
    return (
      <tr>
        <td>{authorizedKey.displayName}</td>
        <td>
          <DateFromNow date={authorizedKey.created} />
        </td>
        <td className="is-hidden-mobile">{formatAuthorizedKey(authorizedKey.raw)}</td>
        <td>{this.renderDeleteAction()}</td>
      </tr>
    );
  }

  renderDeleteAction() {
    const { authorizedKey, t } = this.props;
    const link = authorizedKey._links.delete;
    if (link) {
      const { loading } = this.state;
      return (
        <DeleteButton label={t("scm-ssh-plugin.delete")} loading={loading} action={() => this.onDelete(link.href)} />
      );
    }
    return null;
  }
}

export default withTranslation("plugins")(AuthorizedKeyRow);
