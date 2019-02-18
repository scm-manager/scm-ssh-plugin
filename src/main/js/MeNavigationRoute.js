//@flow
import React from "react";
import { Route } from "react-router-dom";
import AuthorizedKeys from "./AuthorizedKeysPage";
import type { Me } from "@scm-manager/ui-types";

type Props = {
  url: string,
  me: Me
};

class MeNavigationRoute extends React.Component<Props> {
  render() {
    const { me, url } = this.props;
    return (
      <Route
        path={`${url}/settings/authorized_keys`}
        render={() => <AuthorizedKeys link={me._links.authorized_keys.href} />}
      />
    );
  }
}

export default MeNavigationRoute;
