//@flow
import React from "react";
import { apiClient, Subtitle } from "@scm-manager/ui-components";
import { translate } from "react-i18next";
import AuthorizedKeysList from "./AuthorizedKeysList";
import AuthorizedKeysForm from "./AuthorizedKeysForm";
import type {AuthorizedKeysCollection} from "./types";
import Loading from "@scm-manager/ui-components/src/Loading";
import ErrorNotification from "@scm-manager/ui-components/src/ErrorNotification";

type Props = {
  link: string,

  // context props
  t: string => string
};

type State = {
  authorizedKeys?: AuthorizedKeysCollection,
  loading: boolean,
  error?: Error,
};

class AuthorizedKeysPage extends React.Component<Props, State> {
  constructor(props: Props) {
    super(props);
    this.state = {
      loading: false
    };
  }

  componentDidMount() {
    this.fetchKeys();
  }

  fetchKeys = () => {
    this.setState({
      loading: true
    });

    apiClient
      .get(this.props.link)
      .then(resp => resp.json())
      .then(authorizedKeys => {
        this.setState({
          loading: false,
          authorizedKeys,
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

  onKeyAdded = () => {
    this.fetchKeys();
  };

  onKeyDeleted = (error?: Error) => {
    if (error) {
      this.setState({
        error
      });
    } else {
      this.fetchKeys();
    }
  };

  render() {
    const { authorizedKeys, loading, error } = this.state;
    let children;
    if (loading) {
      children = <Loading />;
    } else if (error) {
      children = <ErrorNotification error={error} />;
    } else {

      let form = null;
      if (authorizedKeys && authorizedKeys._links.create) {
        form = <AuthorizedKeysForm url={authorizedKeys._links.create.href} onKeyAdded={this.onKeyAdded} />;
      }

      children = (
        <>
          <AuthorizedKeysList
            authorizedKeys={authorizedKeys}
            link={this.props.link}
            onKeyDeleted={this.onKeyDeleted}
          />
          { form }
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
