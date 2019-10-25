import React from "react";
import { Route } from "react-router-dom";
import { User } from "@scm-manager/ui-types";
import AuthorizedKeys from "./AuthorizedKeysPage";

type Props = {
  url: string;
  user: User;
};

class UserNavigationRoute extends React.Component<Props> {
  render() {
    const { user, url } = this.props;
    return (
      <Route
        path={`${url}/settings/authorized_keys`}
        render={() => <AuthorizedKeys link={user._links.authorized_keys.href} />}
      />
    );
  }
}

export default UserNavigationRoute;
