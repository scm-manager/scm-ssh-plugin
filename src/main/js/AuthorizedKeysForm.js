//@flow
import React from "react";
import { translate } from "react-i18next";
import { InputField, SubmitButton, Textarea } from "@scm-manager/ui-components";
import type { AuthorizedKey } from "./types";

type Props = {
  onSubmit: AuthorizedKey => void,
  // context props
  t: string => string
};

type State = {
  displayName?: string,
  raw?: string
};

class AuthorizedKeysForm extends React.Component<Props, State> {
  constructor(props: Props) {
    super(props);
    this.state = {};
  }

  submit = (e: Event) => {
    e.preventDefault();
    const { displayName, raw } = this.state;
    if (displayName && raw) {
      this.props.onSubmit({
        displayName,
        raw,
        _links: {}
      });
    }
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
    return (
      <form onSubmit={this.submit}>
        <InputField
          name="displayName"
          label={t("scm-ssh-plugin.displayName")}
          onChange={this.onChange}
        />
        <Textarea
          name="raw"
          label={t("scm-ssh-plugin.raw")}
          onChange={this.onChange}
        />
        <SubmitButton
          label={t("scm-ssh-plugin.addKey")}
          disabled={!this.isValid()}
        />
      </form>
    );
  }
}

export default translate("plugins")(AuthorizedKeysForm);
