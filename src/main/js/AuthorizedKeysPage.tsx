/*
 * MIT License
 *
 * Copyright (c) 2020-present Cloudogu GmbH and Contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
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
        <Subtitle subtitle={t("scm-ssh-plugin.title")} />
        {children}
      </>
    );
  };
}

export default withTranslation("plugins")(AuthorizedKeysPage);
