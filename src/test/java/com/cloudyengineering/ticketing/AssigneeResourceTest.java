package com.cloudyengineering.ticketing;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.h2.H2DatabaseTestResource;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;

import java.util.stream.IntStream;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;

@QuarkusTest
@QuarkusTestResource(H2DatabaseTestResource.class)
@QuarkusTestResource(WiremockActions.class)
@TestHTTPEndpoint(AssigneeResource.class)
public class AssigneeResourceTest {

    private final Logger log = LoggerFactory.getLogger(AssigneeResourceTest.class);

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
    void whenGetPagedOffset() {
        createAssignees();
        String results = given()
                .contentType(ContentType.JSON)
                .queryParam("size", 10)
                .queryParam("offset", 3)
                .when()
                .get()
                .then()
                .statusCode(200)
        .body("size()", is(10)).extract().asString();

        log.debug(results);
    }

    void createAssignees() {
        IntStream.range(1, 20).forEach(index ->
                this.assigneeService.createAssignee(String.format("user_%d", index), String.format("user%d@home.com", index))
        );
    }

}
