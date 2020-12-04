package com.cloudyengineering.ticketing;

import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.hamcrest.CoreMatchers.is;
import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;

@QuarkusTest
@TestHTTPEndpoint(TicketResource.class)
public class TicketResourceTest {

    private final Logger log = LoggerFactory.getLogger(TicketResourceTest.class);

    @Test
    void createAssignedTicket() {
        Ticket ticket = new Ticket();
        ticket.setAssigneeLink("12345");
        ticket.setDescription("This is a description");
        ticket.setSummary("This is a summary");

        given()
                .contentType(ContentType.JSON)
                .body(ticket)
                .when()
                .post()
                .then()
                .header("Location", "http://localhost:8081/api/ticket/3")
                .statusCode(201);
    }

    @Test
    void testGetTicketByUrl() {
        Ticket ticket = new Ticket();
        ticket.setAssigneeLink("http://localhost:8083/api/assignee/434322");
        ticket.setDescription("This is a description");
        ticket.setSummary("This is a summary");

        given()
                .contentType(ContentType.JSON)
                .body(ticket)
                .when()
                .post()
                .then()
                .header("Location", "http://localhost:8081/api/ticket/2")
                .statusCode(201);

        given()
                .contentType(ContentType.JSON)
                .when()
                .get("http://localhost:8081/api/ticket/2")
                .then()
                .statusCode(200);
    }

    @Test
    public void testFindTicketsAssignedToUser() {
        given()
                .contentType(ContentType.JSON)
                .queryParam("user_uri", "http://localhost:8083/api/assignee/434322")
                .get("http://localhost:8081/api/ticket")
                .then()
                .statusCode(200)
                .body("$.size()", is(1));
    }
}
