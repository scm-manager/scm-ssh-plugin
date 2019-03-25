package com.cloudogu.scm.ssh.auth;

import org.apache.shiro.mgt.DefaultSecurityManager;
import org.apache.shiro.mgt.DefaultSessionStorageEvaluator;
import org.apache.shiro.mgt.DefaultSubjectDAO;

import javax.inject.Inject;

/**
 * SecurityManager used for ssh authentication and authorization. The {@link SshSecurityManager} should be injected as
 * a {@link javax.inject.Provider} to avoid circular dependency resolution problems.
 */
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
