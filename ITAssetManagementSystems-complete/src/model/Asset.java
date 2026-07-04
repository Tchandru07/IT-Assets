package model;

public class Asset {

    private int assetId;
    private String assetName;
    private String assetType;
    private String serialNumber;
    private int employeeId;
    private AssetStatus status;

    public Asset() {
        this.status = AssetStatus.AVAILABLE;
    }

    public Asset(int assetId, String assetName, String assetType,
                 String serialNumber, int employeeId, AssetStatus status) {

        this.assetId = assetId;
        this.assetName = assetName;
        this.assetType = assetType;
        this.serialNumber = serialNumber;
        this.employeeId = employeeId;
        this.status = status == null ? AssetStatus.AVAILABLE : status;
    }

    public int getAssetId() {
        return assetId;
    }

    public void setAssetId(int assetId) {
        this.assetId = assetId;
    }

    public String getAssetName() {
        return assetName;
    }

    public void setAssetName(String assetName) {
        this.assetName = assetName;
    }

    public String getAssetType() {
        return assetType;
    }

    public void setAssetType(String assetType) {
        this.assetType = assetType;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public int getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(int employeeId) {
        this.employeeId = employeeId;
    }

    public AssetStatus getStatus() {
        return status;
    }

    public void setStatus(AssetStatus status) {
        this.status = status == null ? AssetStatus.AVAILABLE : status;
    }

    @Override
    public String toString() {
        return "Asset{" +
                "assetId=" + assetId +
                ", assetName='" + assetName + '\'' +
                ", assetType='" + assetType + '\'' +
                ", serialNumber='" + serialNumber + '\'' +
                ", employeeId=" + employeeId +
                ", status='" + status + '\'' +
                '}';
    }
}
