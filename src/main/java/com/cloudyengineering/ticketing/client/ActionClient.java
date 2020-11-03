package com.cloudyengineering.ticketing.client;

import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;

@Path("/api/action")
@RegisterRestClient(configKey = "actions-api")
public interface ActionClient {

    @POST
    @Consumes("application/json")
    @Produces("application/json")
    Response createAction(@QueryParam("action_name") String name);

    @GET
    @Produces("application/json")
    @Path("/{actionId}")
    ActionMessage getActionById(@PathParam("actionId") Long actionId);
}
