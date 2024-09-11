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
