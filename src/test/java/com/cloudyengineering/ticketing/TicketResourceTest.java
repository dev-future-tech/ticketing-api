package com.cloudyengineering.ticketing;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.h2.H2DatabaseTestResource;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;

@QuarkusTest
@QuarkusTestResource(H2DatabaseTestResource.class)
@TestHTTPEndpoint(TicketResource.class)
public class TicketResourceTest {

    @Test
    void whenNotImplemented() {
        Ticket ticket = new Ticket();
        ticket.setAssignee("12345");
        ticket.setDescription("This is a description");
        ticket.setSummary("This is a summary");

        given()
                .contentType(ContentType.JSON)
                .body(ticket)
                .when()
                .post()
                .then()
//                .header("Location", "http://localhost:8081/api/ticket/1")
                .statusCode(501);
    }

}
