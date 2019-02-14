package com.cloudogu.scm.ssh.command;

@FunctionalInterface
public interface RepositoryContextResolver {

  RepositoryContext resolve(String[] args);

}
