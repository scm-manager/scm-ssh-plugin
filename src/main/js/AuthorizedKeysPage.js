//@flow
import React from "react";
import { apiClient, Subtitle } from "@scm-manager/ui-components";
import { translate } from "react-i18next";
import AuthorizedKeysList from "./AuthorizedKeysList";
import AuthorizedKeysForm from "./AuthorizedKeysForm";
import type { AuthorizedKey } from "./types";
import Loading from "@scm-manager/ui-components/src/Loading";
import ErrorNotification from "@scm-manager/ui-components/src/ErrorNotification";

type ApiFn = () => Promise<Response>;

type Props = {
  link: string,

  // context props
  t: string => string
};

type State = {
  loading: boolean,
  error?: Error
};

class AuthorizedKeysPage extends React.Component<Props, State> {
  constructor(props: Props) {
    super(props);
    this.state = {
      loading: false
    };
  }

  addKey = (authorizedKey: AuthorizedKey) => {
    const { link } = this.props;
    const fn = () => apiClient.post(link, authorizedKey, "application/vnd.scmm-authorizedkey+json;v=2");
    this.callApi(fn);
  };

  deleteKey = (authorizedKey: AuthorizedKey) => {
    const fn = () => apiClient.delete(authorizedKey._links.delete.href);
    this.callApi(fn);
  };

  callApi = (apiFn: ApiFn) => {
    this.setState({
      loading: true
    });
    apiFn()
      .then(() => {
        this.setState({
          loading: false,
          error: undefined
        });
      })
      .catch(error => {
        this.setState({
          loading: false,
          error
        });
      });
  };

  render() {
    const { loading, error } = this.state;
    let children;
    if (loading) {
      children = <Loading />;
    } else if (error) {
      children = <ErrorNotification error={error} />;
    } else {
      children = (
        <>
          <AuthorizedKeysList
            link={this.props.link}
            onDelete={this.deleteKey}
          />
          <AuthorizedKeysForm onSubmit={this.addKey} />
        </>
      );
    }
    return this.renderWithTitle(children);
  }

  renderWithTitle = children => {
    const { t } = this.props;
    return (
      <>
        <Subtitle subtitle={t("scm-ssh-plugin.title")} />
        {children}
      </>
    );
  };
}

export default translate("plugins")(AuthorizedKeysPage);
