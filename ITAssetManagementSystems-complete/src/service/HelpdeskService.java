package service;

import dao.HelpdeskTicketDAO;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import model.HelpdeskTicket;
import model.TicketPriority;
import model.TicketStatus;

public class HelpdeskService {

    private final List<HelpdeskTicket> tickets = new ArrayList<>();
    private final HelpdeskTicketDAO helpdeskTicketDAO;
    private int nextTicketId = 1;

    public HelpdeskService() {
        this.helpdeskTicketDAO = null;
    }

    public HelpdeskService(HelpdeskTicketDAO helpdeskTicketDAO) {
        this.helpdeskTicketDAO = helpdeskTicketDAO;
    }

    public HelpdeskTicket createTicket(int employeeId, int assetId, String issueTitle,
                                       String description, TicketPriority priority) {
        if (helpdeskTicketDAO != null) {
            return helpdeskTicketDAO.createTicket(employeeId, assetId, issueTitle, description, priority);
        }
        HelpdeskTicket ticket = new HelpdeskTicket(
                nextTicketId++,
                employeeId,
                assetId,
                issueTitle,
                description,
                priority,
                TicketStatus.OPEN,
                LocalDate.now(),
                null
        );
        tickets.add(ticket);
        return ticket;
    }

    public HelpdeskTicket findTicketById(int ticketId) {
        if (helpdeskTicketDAO != null) {
            return helpdeskTicketDAO.findTicketById(ticketId);
        }
        for (HelpdeskTicket ticket : tickets) {
            if (ticket.getTicketId() == ticketId) {
                return ticket;
            }
        }
        return null;
    }

    public void closeTicket(int ticketId) {
        if (helpdeskTicketDAO != null) {
            helpdeskTicketDAO.closeTicket(ticketId);
            return;
        }
        HelpdeskTicket ticket = findTicketById(ticketId);
        if (ticket == null) {
            throw new IllegalArgumentException("Ticket not found.");
        }
        ticket.setStatus(TicketStatus.CLOSED);
        ticket.setResolvedDate(LocalDate.now());
    }

    public List<HelpdeskTicket> getAllTickets() {
        if (helpdeskTicketDAO != null) {
            return helpdeskTicketDAO.getAllTickets();
        }
        return new ArrayList<>(tickets);
    }
}
