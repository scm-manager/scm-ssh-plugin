/*
 * Copyright (c) 2020 - present Cloudogu GmbH
 *
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, version 3.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see https://www.gnu.org/licenses/.
 */

import React from "react";
import { WithTranslation, withTranslation } from "react-i18next";
import { AuthorizedKeysCollection } from "./types";
import { apiClient, Loading, ErrorNotification, Subtitle } from "@scm-manager/ui-components";
import AuthorizedKeysList from "./AuthorizedKeysList";
import AuthorizedKeysForm from "./AuthorizedKeysForm";

type Props = WithTranslation & {
  link: string;
};

type State = {
  authorizedKeys?: AuthorizedKeysCollection;
  loading: boolean;
  error?: Error;
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
    const { t } = this.props;
    const { authorizedKeys, loading, error } = this.state;
    let children;
    if (loading) {
      children = <Loading />;
    } else if (error) {
      children = <ErrorNotification error={error} />;
    } else {
      let form = null;
      if (authorizedKeys && authorizedKeys._links.create) {
        form = (
          <>
            <hr />
            <Subtitle subtitle={t("scm-ssh-plugin.authorizedKeys.addSubtitle")} />
            <AuthorizedKeysForm url={authorizedKeys._links.create.href} onKeyAdded={this.onKeyAdded} />
          </>
        );
      }

      children = (
        <>
          <AuthorizedKeysList authorizedKeys={authorizedKeys} link={this.props.link} onKeyDeleted={this.onKeyDeleted} />
          {form}
        </>
      );
    }
    return this.renderWithTitle(children);
  }

  renderWithTitle = children => {
    const { t } = this.props;
    return (
      <>
        <Subtitle subtitle={t("scm-ssh-plugin.authorizedKeys.title")} />
        <p>{t("scm-ssh-plugin.authorizedKeys.description")}</p>
        <br />
        {children}
      </>
    );
  };
}

export default withTranslation("plugins")(AuthorizedKeysPage);
