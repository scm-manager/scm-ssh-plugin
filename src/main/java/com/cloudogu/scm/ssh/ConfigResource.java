package com.cloudogu.scm.ssh;

import com.webcohesion.enunciate.metadata.rs.ResponseCode;
import com.webcohesion.enunciate.metadata.rs.StatusCodes;
import sonia.scm.config.ConfigurationPermissions;

import javax.inject.Inject;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

@Path("v2/config/ssh")
public class ConfigResource {

  private final ConfigStore configStore;
  private final ConfigurationMapper mapper;

  @Inject
  public ConfigResource(ConfigStore configStore, ConfigurationMapper mapper) {
    this.configStore = configStore;
    this.mapper = mapper;
  }

  @GET
  @Path("")
  @StatusCodes({
    @ResponseCode(code = 200, condition = "success"),
    @ResponseCode(code = 401, condition = "not authenticated / invalid credentials"),
    @ResponseCode(code = 403, condition = "not authorized, the current user does not have the privilege"),
    @ResponseCode(code = 500, condition = "internal server error")})
  @Produces(MediaType.APPLICATION_JSON)
  public ConfigurationDto getConfig() {
    ConfigurationPermissions.read("ssh").check();
    return mapper.map(configStore.getConfiguration());
  }

  @PUT
  @Path("")
  @StatusCodes({
    @ResponseCode(code = 204, condition = "success"),
    @ResponseCode(code = 401, condition = "not authenticated / invalid credentials"),
    @ResponseCode(code = 403, condition = "not authorized, the current user does not have the privilege"),
    @ResponseCode(code = 500, condition = "internal server error")})
  @Consumes(MediaType.APPLICATION_JSON)
  public Response setConfig(@Context UriInfo uriInfo, @NotNull @Valid ConfigurationDto config) {
    ConfigurationPermissions.write("ssh").check();
    Configuration newConfig = mapper.map(config);
    configStore.setConfiguration(newConfig);

    return Response.noContent().build();
  }
}
