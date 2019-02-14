package com.cloudogu.scm.ssh.auth;

import org.apache.shiro.mgt.DefaultSecurityManager;
import org.apache.shiro.mgt.DefaultSessionStorageEvaluator;
import org.apache.shiro.mgt.DefaultSubjectDAO;

import javax.inject.Inject;

public class SshSecurityManager extends DefaultSecurityManager {

  @Inject
  public SshSecurityManager(Realms realms) {
    super(realms.get());

    DefaultSubjectDAO subjectDAO = new DefaultSubjectDAO();
    DefaultSessionStorageEvaluator sessionStorageEvaluator = new DefaultSessionStorageEvaluator();
    sessionStorageEvaluator.setSessionStorageEnabled(false);

    subjectDAO.setSessionStorageEvaluator(sessionStorageEvaluator);
    setSubjectDAO(subjectDAO);
  }
}
