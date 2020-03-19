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
          label={t("scm-ssh-plugin.displayName")}
          value={displayName}
          onChange={this.onChange}
        />
        <Textarea name="raw" label={t("scm-ssh-plugin.raw")} value={raw} onChange={this.onChange} />
        <Level
          right={<SubmitButton label={t("scm-ssh-plugin.addKey")} loading={loading} disabled={!this.isValid()} />}
        />
      </form>
    );
  }
}

export default withTranslation("plugins")(AuthorizedKeysForm);
