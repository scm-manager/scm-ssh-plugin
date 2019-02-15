//@flow
import React from "react";
import type { AuthorizedKey } from "./types";
import DateFromNow from "@scm-manager/ui-components/src/DateFromNow";
import { formatAuthorizedKey } from "./formatAuthorizedKey";
import DeleteAction from "./DeleteAction";

type Props = {
  onDelete: AuthorizedKey => void,
  authorizedKey: AuthorizedKey
};

class AuthorizedKeyRow extends React.Component<Props> {
  render() {
    const { authorizedKey } = this.props;
    return (
      <tr>
        <td>{authorizedKey.displayName}</td>
        <td>
          <DateFromNow date={authorizedKey.created} />
        </td>
        <td className="is-hidden-mobile">
          {formatAuthorizedKey(authorizedKey.raw)}
        </td>
        <td>{this.renderDeleteAction()}</td>
      </tr>
    );
  }

  renderDeleteAction() {
    const { onDelete, authorizedKey } = this.props;
    if (authorizedKey._links.delete) {
      return <DeleteAction action={() => onDelete(authorizedKey)} />;
    }
    return null;
  }
}

export default AuthorizedKeyRow;
