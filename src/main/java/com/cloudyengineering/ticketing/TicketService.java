package com.cloudyengineering.ticketing;

import io.agroal.api.AgroalDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.sql.DataSource;
import java.sql.*;

@ApplicationScoped
public class TicketService {

    private final Logger log = LoggerFactory.getLogger(TicketService.class);

    @Inject
    AgroalDataSource dataSource;


    public Long createTicket(String summary, String description) {
        Long ticketId = null;

        String sql = "insert into tickets(summary, description) values(?, ?)";

        Connection con = null;
        try {
            con = dataSource.getConnection();
            PreparedStatement pstmt = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            pstmt.setString(1, summary);
            pstmt.setString(2, description);

            int inserted = pstmt.executeUpdate();
            if ( inserted > 0) {
                ResultSet rs = pstmt.getGeneratedKeys();

                if (rs.next()) {
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
}
