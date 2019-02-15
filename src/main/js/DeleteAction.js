//@flow
import React from "react";
import injectSheet from "react-jss";

const styles = {
  pointer: {
    cursor: "pointer"
  }
};

type Props = {
  action: () => void,
  // context props
  classes: Object
};

class DeleteAction extends React.Component<Props> {
  render() {
    const { action, classes } = this.props;
    return (
      <a onClick={action} className={classes.pointer}>
        <span className="icon">
          <i className="fas fa-trash" />
        </span>
      </a>
    );
  }
}

export default injectSheet(styles)(DeleteAction);
