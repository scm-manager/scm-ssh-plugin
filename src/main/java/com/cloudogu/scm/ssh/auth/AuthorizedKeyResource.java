package com.cloudogu.scm.ssh.auth;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("v2/authorized_keys")
public class AuthorizedKeyResource {

  private final AuthorizedKeyStore store;

  @Inject
  public AuthorizedKeyResource(AuthorizedKeyStore store) {
    this.store = store;
  }

  @POST
  @Consumes(MediaType.APPLICATION_JSON)
  public Response addKey(AuthorizedKey authorizedKey) {
    Subject subject = SecurityUtils.getSubject();
    String username = subject.getPrincipal().toString();
    store.add(username, authorizedKey);
    return Response.noContent().build();
  }

}
