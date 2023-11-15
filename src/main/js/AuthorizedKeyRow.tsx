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
import styled from "styled-components";
import { apiClient, DateFromNow, Loading, Button } from "@scm-manager/ui-components";
import { AuthorizedKey } from "./types";
import { formatAuthorizedKey } from "./formatAuthorizedKey";

type Props = WithTranslation & {
  onKeyDeleted: (error?: Error) => void;
  authorizedKey: AuthorizedKey;
};

type State = {
  loading: boolean;
};

const VCenteredTd = styled.td`
  display: table-cell;
  vertical-align: middle !important;
`;

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
        <VCenteredTd>{authorizedKey.displayName}</VCenteredTd>
        <VCenteredTd>
          <DateFromNow date={authorizedKey.created} />
        </VCenteredTd>
        <VCenteredTd className="is-hidden-mobile">{formatAuthorizedKey(authorizedKey.raw)}</VCenteredTd>
        <VCenteredTd>{this.renderDeleteAction()}</VCenteredTd>
      </tr>
    );
  }

  renderDeleteAction() {
    const { authorizedKey, t } = this.props;
    const link = authorizedKey._links.delete;
    if (link) {
      const { loading } = this.state;

      if (loading) {
        return <Loading />;
      }
      return (
        <Button
          color="text"
          icon="trash"
          action={() => this.onDelete(link.href)}
          title={t("scm-ssh-plugin.authorizedKeys.delete")}
          className="px-2"
        />
      );
    }
    return null;
  }
}

export default withTranslation("plugins")(AuthorizedKeyRow);
