package com.cloudyengineering.ticketing;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.util.List;

@Path(("/api/ticket"))
public class TicketResource {

    @Inject
    TicketService ticketService;

    @POST
    @Consumes("application/json")
    @Produces("application/json")
    public Response createTicket(Ticket ticket) {

        Long ticketId = this.ticketService.createTicket(ticket.getSummary(), ticket.getDescription(),
                ticket.getAssigneeLink());

        return Response.created(URI.create(String.format("/api/ticket/%d", ticketId))).build();
    }

    @GET
    @Produces("application/json")
    @Path("/{ticket_id}")
    public Response getTicketById(@PathParam("ticket_id") Long ticketId) {
        Ticket found = this.ticketService.getTicketById(ticketId);

        if(found != null) {
            return Response.ok(found).build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }
    @GET
    @Produces("application/json")
    public Response findTicketsAssignedToUser(@QueryParam("user_uri") String userUri) {
        List<Ticket> results = this.ticketService.getTicketsForUser(userUri, 0, 10);
        return Response.ok(results).build();
    }
}
