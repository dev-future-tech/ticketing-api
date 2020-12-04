package com.cloudyengineering.ticketing;

import io.agroal.api.AgroalDataSource;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class TicketService {

    private final Logger log = LoggerFactory.getLogger(TicketService.class);

    @Inject
    AgroalDataSource dataSource;

    @Inject
    @ConfigProperty(name = "multi-cloud.region", defaultValue = "local")
    String region;

    public Long createTicket(String summary, String description, String assignee) {
        Long ticketId = null;

        String sql = "insert into tickets(summary, description, assignee_url, global_id, region) values(?, ?, ?, ?, ?)";

        Connection con = null;
        try {
            con = dataSource.getConnection();
            PreparedStatement pstmt = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            pstmt.setString(1, summary);
            pstmt.setString(2, description);

            if(assignee != null) {
                //may want to perform a check for the requested assignee to make sure it's valid
                // if (httpClient.get(assignee) != null) then ....
                pstmt.setString(3, assignee);
            } else {
                pstmt.setNull(3, Types.VARCHAR);
            }
            pstmt.setString(4, UUID.randomUUID().toString());
            pstmt.setString(5, this.region);

            int inserted = pstmt.executeUpdate();
            if ( inserted > 0) {
                ResultSet rs = pstmt.getGeneratedKeys();

                if (rs.next()) {
                    log.debug("Retrieving ticketId from results...");
                    ticketId = rs.getLong(1);
                }
                rs.close();
            }
            pstmt.close();
        } catch(Exception e) {
            log.error("Error creating ticket", e);
            ticketId = -1L;
        } finally {
            try {
                assert con != null;
                con.close();
            } catch(Exception sqle) {
                log.error(sqle.getMessage(), sqle);
            }
        }

        return ticketId;
    }

    public List<Ticket> getTicketsForUser(String userUri, int offset, int size) {
        String query = "select ticket_id, summary, description, assignee_url from tickets where assignee_url = ? offset ? limit ?";

        List<Ticket> results = new ArrayList<>();
        PreparedStatement pstmt = null;
        try (Connection con = dataSource.getConnection()) {
            pstmt = con.prepareStatement(query);

            pstmt.setString(1, userUri);
            pstmt.setInt(2, offset);
            pstmt.setInt(3, size);

            ResultSet rs = pstmt.executeQuery();

            while(rs.next()) {
                Ticket toReturn = new Ticket();
                toReturn.setSummary(rs.getString("summary"));
                toReturn.setDescription(rs.getString("description"));
                toReturn.setAssigneeLink(rs.getString("assignee_url"));
                toReturn.setTicketId(rs.getLong("ticket_id"));
                results.add(toReturn);
            }

        } catch(Exception e) {
            log.error("Error reading tickets for user {}", userUri, e);
        } finally {
            try {
                assert pstmt != null;
                pstmt.close();
            } catch(Exception e) {
                log.error(e.getMessage(), e);
            }
        }
        return results;
    }

    public Ticket getTicketById(Long ticketId) {
        String sql = "select ticket_id, summary, description, assignee_url from tickets where ticket_id = ?";

        Ticket toReturn = null;

        try (Connection con = this.dataSource.getConnection()) {
            PreparedStatement pstmt = con.prepareStatement(sql);
            pstmt.setLong(1, ticketId);

            ResultSet rs = pstmt.executeQuery();

            if(rs.next()) {
                toReturn = new Ticket();
                toReturn.setSummary(rs.getString("summary"));
                toReturn.setDescription(rs.getString("description"));
                toReturn.setAssigneeLink(rs.getString("assignee_url"));
                toReturn.setTicketId(ticketId);
            }
        } catch(Exception e) {
            log.error("Error retrieving ticket with id {}", ticketId, e);
        }
        return toReturn;
    }
}
