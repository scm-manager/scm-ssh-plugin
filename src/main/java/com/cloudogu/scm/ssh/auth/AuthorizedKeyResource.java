/*
 * Copyright (c) 2020 - present Cloudogu GmbH
 *
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, version 3.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see https://www.gnu.org/licenses/.
 */

package com.cloudogu.scm.ssh.auth;

import de.otto.edison.hal.HalRepresentation;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import sonia.scm.api.v2.resources.ErrorDto;
import sonia.scm.web.VndMediaType;

import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriBuilder;
import jakarta.ws.rs.core.UriInfo;
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
  @Operation(
    summary = "Get all keys for user",
    description = "Returns all keys for the given username.",
    tags = "SSH Plugin",
    operationId = "ssh_get_all_keys"
  )
  @ApiResponse(
    responseCode = "200",
    description = "success",
    content = @Content(
      mediaType = MEDIA_TYPE_COLLECTION,
      schema = @Schema(implementation = HalRepresentation.class)
    )
  )
  @ApiResponse(responseCode = "401", description = "not authenticated / invalid credentials")
  @ApiResponse(responseCode = "403", description = "not authorized /  the current user does not have the right privilege")
  @ApiResponse(
    responseCode = "500",
    description = "internal server error",
    content = @Content(
      mediaType = VndMediaType.ERROR_TYPE,
      schema = @Schema(implementation = ErrorDto.class)
    )
  )
  public HalRepresentation findAll(@PathParam("username") String username) {
    return collectionMapper.map(username, store.getAll(username));
  }

  @GET
  @Path("{id}")
  @Produces(MEDIA_TYPE)
  @Operation(
    summary = "Get single key for user",
    description = "Returns a single key for username by id.",
    tags = "SSH Plugin",
    operationId = "ssh_get_single_key"
  )
  @ApiResponse(
    responseCode = "200",
    description = "success",
    content = @Content(
      mediaType = MEDIA_TYPE,
      schema = @Schema(implementation = AuthorizedKeyDto.class)
    )
  )
  @ApiResponse(responseCode = "401", description = "not authenticated / invalid credentials")
  @ApiResponse(responseCode = "403", description = "not authorized /  the current user does not have the right privilege")
  @ApiResponse(
    responseCode = "404",
    description = "not found / key for given id not available",
    content = @Content(
      mediaType = VndMediaType.ERROR_TYPE,
      schema = @Schema(implementation = ErrorDto.class)
    )
  )
  @ApiResponse(
    responseCode = "500",
    description = "internal server error",
    content = @Content(
      mediaType = VndMediaType.ERROR_TYPE,
      schema = @Schema(implementation = ErrorDto.class)
    )
  )
  public Response findById(@PathParam("username") String username, @PathParam("id") String id) {
    Optional<AuthorizedKey> byId = store.findById(username, id);
    if (byId.isPresent()) {
      return Response.ok(mapper.map(username, byId.get())).build();
    }
    return Response.status(Response.Status.NOT_FOUND).build();
  }

  @POST
  @Path("")
  @Consumes(MEDIA_TYPE)
  @Operation(
    summary = "Create new key",
    description = "Creates new key for user.",
    tags = "SSH Plugin",
    operationId = "ssh_create_key"
  )
  @ApiResponse(responseCode = "201", description = "create success")
  @ApiResponse(responseCode = "401", description = "not authenticated / invalid credentials")
  @ApiResponse(responseCode = "403", description = "not authorized /  the current user does not have the right privilege")
  @ApiResponse(
    responseCode = "500",
    description = "internal server error",
    content = @Content(
      mediaType = VndMediaType.ERROR_TYPE,
      schema = @Schema(implementation = ErrorDto.class)
    )
  )
  public Response create(@Context UriInfo uriInfo, @PathParam("username") String username, AuthorizedKeyDto authorizedKey) {
    String id = store.add(username, mapper.map(authorizedKey));
    UriBuilder builder = uriInfo.getAbsolutePathBuilder();
    builder.path(id);
    return Response.created(builder.build()).build();
  }

  @DELETE
  @Path("{id}")
  @Operation(
    summary = "Deletes key",
    description = "Deletes key for user.",
    tags = "SSH Plugin",
    operationId = "ssh_delete_key"
  )
  @ApiResponse(responseCode = "204", description = "delete success")
  @ApiResponse(responseCode = "401", description = "not authenticated / invalid credentials")
  @ApiResponse(responseCode = "403", description = "not authorized /  the current user does not have the right privilege")
  @ApiResponse(
    responseCode = "500",
    description = "internal server error",
    content = @Content(
      mediaType = VndMediaType.ERROR_TYPE,
      schema = @Schema(implementation = ErrorDto.class)
    )
  )
  public Response deleteById(@PathParam("username") String username, @PathParam("id") String id) {
    store.delete(username, id);
    return Response.noContent().build();
  }
}
