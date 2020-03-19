/**
 * MIT License
 *
 * Copyright (c) 2020-present Cloudogu GmbH and Contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.cloudogu.scm.ssh.auth;

import de.otto.edison.hal.HalRepresentation;
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

@Path("v2/authorized_keys/{username}")
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
  public HalRepresentation findAll(@PathParam("username") String username) {
    return collectionMapper.map(username, store.getAll(username));
  }

  @GET
  @Path("{id}")
  @Produces(MEDIA_TYPE)
  public Response findById(@PathParam("username") String username, @PathParam("id") String id) {
    Optional<AuthorizedKey> byId = store.findById(username, id);
    if (byId.isPresent()) {
      return Response.ok( mapper.map(username, byId.get()) ).build();
    }
    return Response.status(Response.Status.NOT_FOUND).build();
  }

  @POST
  @Path("")
  @Consumes(MEDIA_TYPE)
  public Response create(@Context UriInfo uriInfo, @PathParam("username") String username, AuthorizedKeyDto authorizedKey) {
    String id = store.add(username, mapper.map(authorizedKey));
    UriBuilder builder = uriInfo.getAbsolutePathBuilder();
    builder.path(id);
    return Response.created(builder.build()).build();
  }

  @DELETE
  @Path("{id}")
  public Response deleteById(@PathParam("username") String username, @PathParam("id") String id) {
    store.delete(username, id);
    return Response.noContent().build();
  }
}
