package com.cloudyengineering.ticketing;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@ApplicationScoped
public class AssigneeService {

    private final Logger log = LoggerFactory.getLogger(AssigneeService.class);

    @Inject
    DataSource dataSource;

    public void deleteAssigneeById(Long assigneeId) {
        String sql = "delete from assignees where user_id = ?";

        try (Connection con = dataSource.getConnection()) {
            PreparedStatement pstmt = con.prepareStatement(sql);
            pstmt.setLong(1, assigneeId);

            int deletedRows = pstmt.executeUpdate();

            if (deletedRows < 1) {
                log.info("No assignees deleted with id {}", assigneeId);
            }
        } catch(Exception sqle) {
            log.error("Error deleting assignee {}", assigneeId, sqle);
        }
    }

    public Long createAssignee(String username, String emailAddr) {
        String sql = "insert into assignees (username, email_addr) values (?, ?)";
        Long assigneeId = null;

        try (Connection con = dataSource.getConnection()) {
            PreparedStatement pstmt = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            pstmt.setString(1, username);
            pstmt.setString(2, emailAddr);
            int inserted = pstmt.executeUpdate();

            if (inserted > 0) {
                ResultSet rs = pstmt.getGeneratedKeys();

                if (rs.next()) {
                    assigneeId = rs.getLong(1);
                }
            }
        } catch(SQLException sqle) {
            log.error("Error creating user {}", username, sqle);
            assigneeId = -1L;
        }
        return assigneeId;
    }

    public Assignee getAssigneeById(Long assigneeId) {
        String sql = "select * from assignees where user_id = ?";

        Assignee assignee = null;
        try (Connection con = dataSource.getConnection()) {
            PreparedStatement pstmt  = con.prepareStatement(sql);
            pstmt.setLong(1, assigneeId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                assignee = new Assignee();
                assignee.setAssigneeId(rs.getLong("user_id"));
                assignee.setUsername(rs.getString("username"));
                assignee.setEmailAddr(rs.getString("email_addr"));
            }
        } catch (Exception sqle) {
            log.error("Error getting assignee with id {}", assigneeId, sqle);
        }
        return assignee;
    }

    public List<Assignee> getPagedAssignees(int offset, int size) {
        String query = "select * from assignees limit ? offset ?";
        List<Assignee> results = new ArrayList<>();
        try (Connection con = dataSource.getConnection()) {
            PreparedStatement pstmt = con.prepareStatement(query);
            pstmt.setInt(1, size);
            pstmt.setInt(2, offset);

            ResultSet rs = pstmt.executeQuery();

            while(rs.next()) {
                Assignee assignee = new Assignee();
                assignee.setAssigneeId(rs.getLong("assignee_id"));
                assignee.setUsername(rs.getString("username"));
                assignee.setEmailAddr(rs.getString("email_addr"));
                results.add(assignee);
            }
        } catch (SQLException sqle) {
            log.error("Error getting assignees", sqle);
            return Collections.emptyList();
        }
        return results;
    }
}
