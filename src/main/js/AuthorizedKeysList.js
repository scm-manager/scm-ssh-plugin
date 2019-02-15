//@flow
import React from "react";
import { translate } from "react-i18next";
import {
  Loading,
  ErrorNotification,
  apiClient,
  Notification
} from "@scm-manager/ui-components";
import type { Collection } from "@scm-manager/ui-types";
import type { AuthorizedKey } from "./types";
import AuthorizedKeysTable from "./AuthorizedKeysTable";

type AuthorizedKeysCollection = Collection & {
  _embedded: {
    keys: AuthorizedKey[]
  }
};

type Props = {
  link: string,
  onDelete: AuthorizedKey => void,
  // context props
  t: (string) => string
};

type State = {
  authorizedKeys?: AuthorizedKeysCollection,
  loading: boolean,
  error?: Error
};

class AuthorizedKeysList extends React.Component<Props, State> {
  constructor(props: Props) {
    super(props);
    this.state = {
      loading: true
    };
  }

  componentDidMount() {
    apiClient
      .get(this.props.link)
      .then(resp => resp.json())
      .then(authorizedKeys => {
        this.setState({
          loading: false,
          authorizedKeys
        });
      })
      .catch(error => {
        this.setState({
          loading: false,
          error
        });
      });
  }

  render() {
    const { t } = this.props;
    const { loading, error, authorizedKeys } = this.state;
    if (loading) {
      return <Loading />;
    }
    if (error) {
      return <ErrorNotification />;
    }

    if (!authorizedKeys || authorizedKeys._embedded.keys.length <= 0) {
      return (
        <Notification type="info">
          {t("scm-ssh-plugin.noStoredKeys")}
        </Notification>
      );
    }

    const { onDelete } = this.props;
    return (
      <AuthorizedKeysTable
        authorizedKeys={authorizedKeys._embedded.keys}
        onDelete={onDelete}
      />
    );
  }
}

export default translate("plugins")(AuthorizedKeysList);
