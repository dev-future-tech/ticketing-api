package com.cloudyengineering.ticketing;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.util.List;

@Path("/api/assignee")
public class AssigneeResource {

    private final Logger log = LoggerFactory.getLogger(AssigneeResource.class);

    @Inject()
    AssigneeService service;

    @POST
    @Consumes(value={"application/json"})
    @Produces(value={"application/json"})
    public Response createAssignee(@QueryParam("username") String username, @QueryParam("email_addr") String email) {
        if (username == null) {
            log.debug("Missing username!");
            return Response.status(Response.Status.BAD_REQUEST.getStatusCode(), "Username is required").build();
        }

        log.debug("Create user with username {} and email {}", username, email);

        Long assigneeId = this.service.createAssignee(username, email);
        return Response.created(URI.create(String.format("/api/assignee/%d", assigneeId))).build();
    }

    @GET
    @Produces(value={"application/json"})
    @Path("/{assigneeId}")
    public Response getAssigneeById(@PathParam("assigneeId") Long assigneeId) {
        Assignee assignee = this.service.getAssigneeById(assigneeId);
        if (assignee == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(assignee).build();
    }

    @GET
    @Produces(value = {"application/json"})
    public Response getPaginatedAssignees(@QueryParam("size") Integer size, @QueryParam("offset") Integer offset) {
        if (size == null) {
            size = 10;
        }

        if (offset == null) {
            offset = 0;
        }

        List<Assignee> results = this.service.getPagedAssignees(offset, size);
        return Response.ok(results).build();
    }

    @PUT
    @Consumes(value = {"application/json"})
    @Produces(value = {"application/json"})
    @Path("/{assigneeId}")
    public Response updateAssignee(@PathParam("assigneeId") Long assigneeId, @QueryParam("username") String username,
                                   @QueryParam("email_addr") String emailAddr) {
        Assignee assignee = this.service.getAssigneeById(assigneeId);

        if(assignee == null) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        if(username != null) {
            assignee.setUsername(username);
        }

        if(emailAddr != null) {
            assignee.setEmailAddr(emailAddr);
        }

        this.service.saveAssignee(assignee);
        return Response.accepted().build();
    }
}
