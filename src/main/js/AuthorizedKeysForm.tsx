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
import { AuthorizedKey } from "./types";
import { apiClient, ErrorNotification, InputField, SubmitButton, Textarea, Level } from "@scm-manager/ui-components";

type Props = WithTranslation & {
  url: string;
  onKeyAdded: (p: AuthorizedKey) => void;
};

type State = {
  displayName?: string;
  raw?: string;

  loading: boolean;
  error?: Error;
};

class AuthorizedKeysForm extends React.Component<Props, State> {
  constructor(props: Props) {
    super(props);
    this.state = {
      loading: false
    };
  }

  submit = (e: Event) => {
    e.preventDefault();
    const { displayName, raw } = this.state;
    if (displayName && raw) {
      this.addKey({
        displayName,
        raw,
        _links: {}
      });
    }
  };

  addKey = (key: AuthorizedKey) => {
    this.setState({
      loading: true
    });

    const { url, onKeyAdded } = this.props;
    apiClient
      .post(url, key, "application/vnd.scmm-authorizedkey+json;v=2")
      .then(() => {
        this.setState({
          displayName: "",
          raw: "",
          loading: false,
          error: undefined
        });

        onKeyAdded(key);
      })
      .catch(error =>
        this.setState({
          error,
          loading: false
        })
      );
  };

  onChange = (value: string, name: string) => {
    this.setState({
      [name]: value
    });
  };

  isValid = () => {
    const { displayName, raw } = this.state;
    return !!displayName && !!raw;
  };

  render() {
    const { t } = this.props;
    const { displayName, raw, loading, error } = this.state;
    return (
      <form onSubmit={this.submit}>
        <ErrorNotification error={error} />
        <InputField
          name="displayName"
          label={t("scm-ssh-plugin.authorizedKeys.displayName")}
          value={displayName}
          onChange={this.onChange}
        />
        <Textarea name="raw" label={t("scm-ssh-plugin.authorizedKeys.raw")} value={raw} onChange={this.onChange} />
        <Level
          right={<SubmitButton label={t("scm-ssh-plugin.authorizedKeys.addKey")} loading={loading} disabled={!this.isValid()} />}
        />
      </form>
    );
  }
}

export default withTranslation("plugins")(AuthorizedKeysForm);
