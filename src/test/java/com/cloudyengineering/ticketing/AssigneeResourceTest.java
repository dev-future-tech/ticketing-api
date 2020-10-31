package com.cloudyengineering.ticketing;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.h2.H2DatabaseTestResource;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;

import static io.restassured.RestAssured.given;

@QuarkusTest
@QuarkusTestResource(H2DatabaseTestResource.class)
@TestHTTPEndpoint(AssigneeResource.class)
public class AssigneeResourceTest {

    @Inject()
    AssigneeService assigneeService;

    @Test
    void whenCreateAssignee() {
        given().contentType(ContentType.JSON)
                .queryParam("username", "user1")
                .queryParam("email_addr", "user1@home.com")
                .when().post()
                .then().header("Location", "http://localhost:8081/api/assignee/1").statusCode(201);
    }

    @Test
    void whenGettingFoundAssignee() {

        Long createdId = assigneeService.createAssignee("Michael", "michael@home.com");

        given().contentType(ContentType.JSON)
                .basePath("api/assignee")
                .log().all()
                .when()
                .get("/{assigneeId}", createdId)
                .then()
                .statusCode(200);
    }

    @Test
    void whenGettingNotFoundAssignee() {

        given().contentType(ContentType.JSON)
                .basePath("api/assignee")
                .log().all()
                .when()
                .get("/{assigneeId}", 52)
                .then()
                .statusCode(404);
    }

    @Test
    void whenNotImplemented() {
        given()
                .contentType(ContentType.JSON)
                .queryParam("page", 1)
                .queryParam("offset", 0)
                .when()
                .get()
                .then()
                .statusCode(501);
    }
}
