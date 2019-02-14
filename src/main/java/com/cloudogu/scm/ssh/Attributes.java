package com.cloudogu.scm.ssh;

import org.apache.shiro.subject.Subject;
import static org.apache.sshd.common.AttributeRepository.AttributeKey;

public final class Attributes {

  private Attributes() {
  }

  public static final AttributeKey<Subject> SUBJECT = new AttributeKey();
}
