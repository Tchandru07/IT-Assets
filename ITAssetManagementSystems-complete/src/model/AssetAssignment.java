package model;

import java.time.LocalDate;

public class AssetAssignment {

    private int assignmentId;
    private int assetId;
    private int employeeId;
    private LocalDate assignedDate;
    private LocalDate returnDate;
    private AssignmentStatus status;

    public AssetAssignment() {
        this.status = AssignmentStatus.ACTIVE;
    }

    public AssetAssignment(int assignmentId, int assetId, int employeeId,
                           LocalDate assignedDate, LocalDate returnDate, AssignmentStatus status) {
        this.assignmentId = assignmentId;
        this.assetId = assetId;
        this.employeeId = employeeId;
        this.assignedDate = assignedDate;
        this.returnDate = returnDate;
        this.status = status == null ? AssignmentStatus.ACTIVE : status;
    }

    public int getAssignmentId() {
        return assignmentId;
    }

    public void setAssignmentId(int assignmentId) {
        this.assignmentId = assignmentId;
    }

    public int getAssetId() {
        return assetId;
    }

    public void setAssetId(int assetId) {
        this.assetId = assetId;
    }

    public int getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(int employeeId) {
        this.employeeId = employeeId;
    }

    public LocalDate getAssignedDate() {
        return assignedDate;
    }

    public void setAssignedDate(LocalDate assignedDate) {
        this.assignedDate = assignedDate;
    }

    public LocalDate getReturnDate() {
        return returnDate;
    }

    public void setReturnDate(LocalDate returnDate) {
        this.returnDate = returnDate;
    }

    public AssignmentStatus getStatus() {
        return status;
    }

    public void setStatus(AssignmentStatus status) {
        this.status = status == null ? AssignmentStatus.ACTIVE : status;
    }

    @Override
    public String toString() {
        return "AssetAssignment{" +
                "assignmentId=" + assignmentId +
                ", assetId=" + assetId +
                ", employeeId=" + employeeId +
                ", assignedDate=" + assignedDate +
                ", returnDate=" + returnDate +
                ", status='" + status + '\'' +
                '}';
    }
}
