package dao;

import db.DBConnection;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import model.HelpdeskTicket;
import model.TicketPriority;
import model.TicketStatus;

public class HelpdeskTicketDAO {

    public HelpdeskTicket createTicket(int employeeId, int assetId, String issueTitle,
                                       String description, TicketPriority priority) {
        String sql = "INSERT INTO helpdesk_tickets "
                + "(employee_id, asset_id, issue_title, description, priority, status, created_date, resolved_date) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, NULL)";
        LocalDate createdDate = LocalDate.now();

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setInt(1, employeeId);
            statement.setInt(2, assetId);
            statement.setString(3, issueTitle);
            statement.setString(4, description);
            statement.setString(5, priority.name());
            statement.setString(6, TicketStatus.OPEN.name());
            statement.setDate(7, Date.valueOf(createdDate));
            statement.executeUpdate();

            try (ResultSet keys = statement.getGeneratedKeys()) {
                if (!keys.next()) {
                    throw new IllegalStateException("Ticket ID was not generated.");
                }
                return new HelpdeskTicket(
                        keys.getInt(1),
                        employeeId,
                        assetId,
                        issueTitle,
                        description,
                        priority,
                        TicketStatus.OPEN,
                        createdDate,
                        null
                );
            }
        } catch (SQLException exception) {
            throw new IllegalStateException("Unable to create ticket.", exception);
        }
    }

    public HelpdeskTicket findTicketById(int ticketId) {
        String sql = "SELECT ticket_id, employee_id, asset_id, issue_title, description, priority, "
                + "status, created_date, resolved_date FROM helpdesk_tickets WHERE ticket_id = ?";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, ticketId);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return mapTicket(resultSet);
                }
            }
        } catch (SQLException exception) {
            throw new IllegalStateException("Unable to find ticket.", exception);
        }

        return null;
    }

    public void closeTicket(int ticketId) {
        String sql = "UPDATE helpdesk_tickets SET status = ?, resolved_date = ? WHERE ticket_id = ?";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, TicketStatus.CLOSED.name());
            statement.setDate(2, Date.valueOf(LocalDate.now()));
            statement.setInt(3, ticketId);
            int updatedRows = statement.executeUpdate();

            if (updatedRows == 0) {
                throw new IllegalArgumentException("Ticket not found.");
            }
        } catch (SQLException exception) {
            throw new IllegalStateException("Unable to close ticket.", exception);
        }
    }

    public List<HelpdeskTicket> getAllTickets() {
        String sql = "SELECT ticket_id, employee_id, asset_id, issue_title, description, priority, "
                + "status, created_date, resolved_date FROM helpdesk_tickets ORDER BY ticket_id";
        List<HelpdeskTicket> tickets = new ArrayList<>();

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                tickets.add(mapTicket(resultSet));
            }
        } catch (SQLException exception) {
            throw new IllegalStateException("Unable to list tickets.", exception);
        }

        return tickets;
    }

    private HelpdeskTicket mapTicket(ResultSet resultSet) throws SQLException {
        Date resolvedDate = resultSet.getDate("resolved_date");
        return new HelpdeskTicket(
                resultSet.getInt("ticket_id"),
                resultSet.getInt("employee_id"),
                resultSet.getInt("asset_id"),
                resultSet.getString("issue_title"),
                resultSet.getString("description"),
                TicketPriority.valueOf(resultSet.getString("priority")),
                TicketStatus.valueOf(resultSet.getString("status")),
                resultSet.getDate("created_date").toLocalDate(),
                resolvedDate == null ? null : resolvedDate.toLocalDate()
        );
    }
}
