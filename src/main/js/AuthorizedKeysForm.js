//@flow
import React from "react";
import { translate } from "react-i18next";
import {apiClient, ErrorNotification, InputField, SubmitButton, Textarea} from "@scm-manager/ui-components";
import type { AuthorizedKey } from "./types";

type Props = {
  url: string,
  onKeyAdded: AuthorizedKey => void,
  // context props
  t: string => string
};

type State = {
  displayName?: string,
  raw?: string,

  loading: boolean,
  error?: Error,
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
      this.addKey({displayName, raw, _links: {}});
    }
  };

  addKey = (key: AuthorizedKey) => {
    this.setState({
      loading: true
    });

    const { url, onKeyAdded } = this.props;
    apiClient.post(url, key, "application/vnd.scmm-authorizedkey+json;v=2")
      .then(() => {
        this.setState({
          displayName: "",
          raw: "",
          loading: false,
          error: undefined
        });

        onKeyAdded(key);
      })
      .catch(error => this.setState({
        error,
        loading: false
      }));
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
        <Textarea
          name="raw"
          label={t("scm-ssh-plugin.raw")}
          value={raw}
          onChange={this.onChange}
        />
        <SubmitButton
          label={t("scm-ssh-plugin.addKey")}
          loading={loading}
          disabled={!this.isValid()}
        />
      </form>
    );
  }
}

export default translate("plugins")(AuthorizedKeysForm);
