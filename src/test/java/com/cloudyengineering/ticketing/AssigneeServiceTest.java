package com.cloudyengineering.ticketing;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.h2.H2DatabaseTestResource;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.List;
import java.util.stream.IntStream;

@QuarkusTest
@QuarkusTestResource(H2DatabaseTestResource.class)
public class AssigneeServiceTest {

    private final Logger log = LoggerFactory.getLogger(AssigneeServiceTest.class);
    @Inject()
    AssigneeService service;

    @Test
    public void testCreateAssignee() {
        Long assigneeId = service.createAssignee("user1", "user@home.com");

        Assertions.assertTrue(assigneeId > 0, "Assignee Id is less than 0");

        Assignee assignee = service.getAssigneeById(assigneeId);
        Assertions.assertNotNull(assignee, "Null assignee returned");
    }

    @Test
    public void testCreateAssigneeFailure() {
        Long assigneeId = service.createAssignee(null,
                "usersdfsdfsdfsdfsd@home.com");
        Assertions.assertTrue(assigneeId < 0, "Create succeeded");
    }

    @Test
    public void testFindNonExistentAssignee() {
        Assignee assignee = service.getAssigneeById(53L);
        Assertions.assertNull(assignee);
    }

    @Test
    public void testFailedAssigneeRetrieval() {
        Assignee assignee = service.getAssigneeById(null);
        Assertions.assertNull(assignee);
    }
    @Test
    public void testDeleteAssignee() {
        Long assigneeId = service.createAssignee("user1", "user@home.com");
        service.deleteAssigneeById(assigneeId);
        Assignee assignee = service.getAssigneeById(assigneeId);
        Assertions.assertNull(assignee);
    }

    @Test
    public void testDeleteNonExistentAssignee() {
        service.deleteAssigneeById(10L);
    }

    @Test
    public void testDeleteNullAssignee() {
        service.deleteAssigneeById(null);
    }

    @Test
    void testBatchAssignees() {
        createAssignees();
        List<Assignee> results = this.service.getPagedAssignees(2, 4);
        log.debug("Total results: {}", results.size());
        Assertions.assertTrue(results.size() == 4);
    }

    void createAssignees() {
        IntStream.range(1, 20).forEach(index ->
                this.service.createAssignee(String.format("user_%d", index), String.format("user%d@home.com", index))
        );
    }

}
