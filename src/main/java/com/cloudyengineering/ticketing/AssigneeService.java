package com.cloudyengineering.ticketing;

import io.agroal.api.AgroalDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.IntStream;

@ApplicationScoped
public class AssigneeService {

    private final Logger log = LoggerFactory.getLogger(AssigneeService.class);

    @Inject
    DataSource dataSource;

    public void deleteAssigneeById(Long assigneeId) {
        String sql = "delete from assignees where user_id = ?";

        Connection con = null;
        PreparedStatement pstmt = null;
        try {
            con = dataSource.getConnection();
            pstmt = con.prepareStatement(sql);
            pstmt.setLong(1, assigneeId);

            int deletedRows = pstmt.executeUpdate();

            if (deletedRows < 1) {
                log.info("No assignees deleted with id {}", assigneeId);
            }
//            pstmt.close();
        } catch(Exception sqle) {
            log.error("Error deleting assignee {}", assigneeId, sqle);
        } finally {
            try{
                assert pstmt != null;
                pstmt.close();

//                assert con != null;
//                con.close();
            } catch(Exception e) {
                log.error(e.getMessage(), e);
            }
        }
    }

    public Long createAssignee(String username, String emailAddr) {
        String sql = "insert into assignees (username, email_addr) values (?, ?)";
        log.debug("Creating user {} with email {}", username, emailAddr);
        Long assigneeId = null;

        Connection con = null;
        PreparedStatement pstmt = null;
        try {
            con = dataSource.getConnection();
            pstmt = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            pstmt.setString(1, username);
            pstmt.setString(2, emailAddr);
            int inserted = pstmt.executeUpdate();

            if (inserted > 0) {
                ResultSet rs = pstmt.getGeneratedKeys();

                if (rs.next()) {
                    assigneeId = rs.getLong(1);
                }
                rs.close();
            }
            pstmt.close();
        } catch(Exception sqle) {
            log.error("Error creating user {}", username, sqle);
            assigneeId = -1L;
        } finally {
            try {
                assert con != null;
                con.close();

                assert pstmt != null;
                pstmt.close();
            } catch(Exception e) {
                log.error(e.getMessage(), e);
            }
        }
        return assigneeId;
    }

    public Assignee getAssigneeById(Long assigneeId) {
        String sql = "select * from assignees where user_id = ?";

        Assignee assignee = null;
        Connection con = null;
        PreparedStatement pstmt = null;

        try {
            con = dataSource.getConnection();
            pstmt = con.prepareStatement(sql);
            pstmt.setLong(1, assigneeId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                assignee = new Assignee();
                assignee.setAssigneeId(rs.getLong("user_id"));
                assignee.setUsername(rs.getString("username"));
                assignee.setEmailAddr(rs.getString("email_addr"));
            }
            rs.close();
            pstmt.close();
        } catch (Exception sqle) {
            log.error("Error getting assignee with id {}", assigneeId, sqle);
        } finally {
            try {
//                assert con != null;
//                con.close();

                assert pstmt != null;
                pstmt.close();
            } catch(Exception e) {
                log.error(e.getMessage(), e);
            }
        }
        return assignee;
    }

    public List<Assignee> getPagedAssignees(int offset, int size) {

        String query = "select * from assignees order by user_id limit ? offset ?";
        List<Assignee> results = new ArrayList<>();

        Connection con = null;
        PreparedStatement pstmt = null;
        try {
            con = dataSource.getConnection();
            pstmt = con.prepareStatement(query);
            pstmt.setInt(1, size);
            pstmt.setInt(2, offset);

            ResultSet rs = pstmt.executeQuery();

            while(rs.next()) {
                Assignee assignee = new Assignee();
                assignee.setAssigneeId(rs.getLong("user_id"));
                assignee.setUsername(rs.getString("username"));
                assignee.setEmailAddr(rs.getString("email_addr"));
                results.add(assignee);
            }
            rs.close();
            pstmt.close();
        } catch (SQLException sqle) {
            log.error("Error getting assignees", sqle);
            return Collections.emptyList();
        } finally {
            try {
                assert con != null;
                con.close();

                assert pstmt != null;
                pstmt.close();

            } catch(Exception e) {
                log.error(e.getMessage(), e);
            }
        }
        return results;
    }

    public void saveAssignee(Assignee assignee) {
        String update = "update assignees set username = ?, email_addr = ? where user_id = ?";

        Connection con = null;
        PreparedStatement pstmt = null;
        try {
            con = this.dataSource.getConnection();
            pstmt = con.prepareStatement(update);
            pstmt.setString(1, assignee.getUsername());
            pstmt.setString(2, assignee.getEmailAddr());
            pstmt.setLong(3, assignee.getAssigneeId());

            int updated = pstmt.executeUpdate();

            if (updated < 1) {
                log.error("Nothing updated.");
            }

            pstmt.close();
        } catch (Exception e) {
            log.error("Error updating assignee {}", assignee.getAssigneeId(), e);
        } finally {
            try {
                assert con != null;
                con.close();

                assert pstmt != null;
                pstmt.close();
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        }
    }
}
