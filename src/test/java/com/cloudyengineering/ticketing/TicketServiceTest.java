package com.cloudyengineering.ticketing;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;

@QuarkusTest
public class TicketServiceTest {

    private final Logger log = LoggerFactory.getLogger(TicketServiceTest.class);

    @Inject
    TicketService ticketService;

    @Test
    public void testCreateTicket() {
        Long ticketId = this.ticketService.createTicket("Basic piece of work",
                "Just some work that is basic",
                "http://localhost:8083/api/assignee/1");

        log.debug("Returned ticket id is {}", ticketId);
        Assertions.assertTrue(ticketId != null);

    }
}
