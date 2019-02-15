package com.cloudogu.scm.ssh.auth;

import de.otto.edison.hal.HalRepresentation;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import sonia.scm.web.VndMediaType;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;
import java.util.Optional;

@Path("v2/authorized_keys")
public class AuthorizedKeyResource {

  private static final String MEDIA_TYPE = VndMediaType.PREFIX + "authorizedKey" + VndMediaType.SUFFIX;
  private static final String MEDIA_TYPE_COLLECTION = VndMediaType.PREFIX + "authorizedKeyCollection" + VndMediaType.SUFFIX;

  private final AuthorizedKeyStore store;
  private final AuthorizedKeyCollectionMapper collectionMapper;
  private final AuthorizedKeyMapper mapper;

  @Inject
  public AuthorizedKeyResource(AuthorizedKeyStore store, AuthorizedKeyCollectionMapper collectionMapper, AuthorizedKeyMapper mapper) {
    this.store = store;
    this.collectionMapper = collectionMapper;
    this.mapper = mapper;
  }

  @GET
  @Path("")
  @Produces(MEDIA_TYPE_COLLECTION)
  public HalRepresentation findAll() {
    String username = getPrincipalAsString();
    return collectionMapper.map(store.getAll(username));
  }

  @GET
  @Path("{id}")
  @Produces(MEDIA_TYPE)
  public Response findById(@PathParam("id") String id) {
    String username = getPrincipalAsString();
    Optional<AuthorizedKey> byId = store.findById(username, id);
    if (byId.isPresent()) {
      return Response.ok( mapper.map(byId.get()) ).build();
    }
    return Response.status(Response.Status.NOT_FOUND).build();
  }

  @POST
  @Path("")
  @Consumes(MEDIA_TYPE)
  public Response addKey(@Context UriInfo uriInfo, AuthorizedKeyDto authorizedKey) {
    String username = getPrincipalAsString();
    String id = store.add(username, mapper.map(authorizedKey));
    UriBuilder builder = uriInfo.getAbsolutePathBuilder();
    builder.path(id);
    return Response.created(builder.build()).build();
  }

  @DELETE
  @Path("{id}")
  public Response deleteById(@PathParam("id") String id) {
    String username = getPrincipalAsString();
    store.delete(username, id);
    return Response.noContent().build();
  }

  private String getPrincipalAsString() {
    Subject subject = SecurityUtils.getSubject();
    return subject.getPrincipal().toString();
  }

}
