package com.cloudyengineering.ticketing;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

@Path(("/api/ticket"))
public class TicketResource {

    @Inject
    TicketService ticketService;

    @POST
    @Consumes("application/json")
    public Response createTicket(Ticket ticket) {

        return Response.status(Response.Status.NOT_IMPLEMENTED).build();
    }
}
