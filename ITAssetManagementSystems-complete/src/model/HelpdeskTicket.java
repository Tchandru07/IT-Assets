package model;

import java.time.LocalDate;

public class HelpdeskTicket {

    private int ticketId;
    private int employeeId;
    private int assetId;
    private String issueTitle;
    private String description;
    private TicketPriority priority;
    private TicketStatus status;
    private LocalDate createdDate;
    private LocalDate resolvedDate;

    public HelpdeskTicket() {
        this.priority = TicketPriority.MEDIUM;
        this.status = TicketStatus.OPEN;
    }

    public HelpdeskTicket(int ticketId, int employeeId, int assetId, String issueTitle,
                          String description, TicketPriority priority, TicketStatus status,
                          LocalDate createdDate, LocalDate resolvedDate) {
        this.ticketId = ticketId;
        this.employeeId = employeeId;
        this.assetId = assetId;
        this.issueTitle = issueTitle;
        this.description = description;
        this.priority = priority == null ? TicketPriority.MEDIUM : priority;
        this.status = status == null ? TicketStatus.OPEN : status;
        this.createdDate = createdDate;
        this.resolvedDate = resolvedDate;
    }

    public int getTicketId() {
        return ticketId;
    }

    public void setTicketId(int ticketId) {
        this.ticketId = ticketId;
    }

    public int getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(int employeeId) {
        this.employeeId = employeeId;
    }

    public int getAssetId() {
        return assetId;
    }

    public void setAssetId(int assetId) {
        this.assetId = assetId;
    }

    public String getIssueTitle() {
        return issueTitle;
    }

    public void setIssueTitle(String issueTitle) {
        this.issueTitle = issueTitle;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public TicketPriority getPriority() {
        return priority;
    }

    public void setPriority(TicketPriority priority) {
        this.priority = priority == null ? TicketPriority.MEDIUM : priority;
    }

    public TicketStatus getStatus() {
        return status;
    }

    public void setStatus(TicketStatus status) {
        this.status = status == null ? TicketStatus.OPEN : status;
    }

    public LocalDate getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(LocalDate createdDate) {
        this.createdDate = createdDate;
    }

    public LocalDate getResolvedDate() {
        return resolvedDate;
    }

    public void setResolvedDate(LocalDate resolvedDate) {
        this.resolvedDate = resolvedDate;
    }

    @Override
    public String toString() {
        return "HelpdeskTicket{" +
                "ticketId=" + ticketId +
                ", employeeId=" + employeeId +
                ", assetId=" + assetId +
                ", issueTitle='" + issueTitle + '\'' +
                ", description='" + description + '\'' +
                ", priority='" + priority + '\'' +
                ", status='" + status + '\'' +
                ", createdDate=" + createdDate +
                ", resolvedDate=" + resolvedDate +
                '}';
    }
}
