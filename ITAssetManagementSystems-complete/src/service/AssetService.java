package service;

import dao.AssetDAO;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import model.Asset;
import model.AssetAssignment;
import model.AssetStatus;
import model.AssignmentStatus;

public class AssetService {

    private final List<Asset> assets = new ArrayList<>();
    private final List<AssetAssignment> assignments = new ArrayList<>();
    private final AssetDAO assetDAO;
    private int nextAssignmentId = 1;

    public AssetService() {
        this.assetDAO = null;
    }

    public AssetService(AssetDAO assetDAO) {
        this.assetDAO = assetDAO;
    }

    public void addAsset(Asset asset) {
        if (asset == null) {
            throw new IllegalArgumentException("Asset cannot be null.");
        }
        if (assetDAO != null) {
            assetDAO.addAsset(asset);
            return;
        }
        if (findAssetById(asset.getAssetId()) != null) {
            throw new IllegalArgumentException("Asset ID already exists.");
        }
        assets.add(asset);
    }

    public Asset findAssetById(int assetId) {
        if (assetDAO != null) {
            return assetDAO.findAssetById(assetId);
        }
        for (Asset asset : assets) {
            if (asset.getAssetId() == assetId) {
                return asset;
            }
        }
        return null;
    }

    public AssetAssignment assignAsset(int assetId, int employeeId) {
        if (assetDAO != null) {
            return assetDAO.assignAsset(assetId, employeeId);
        }
        Asset asset = findAssetById(assetId);
        if (asset == null) {
            throw new IllegalArgumentException("Asset not found.");
        }
        if (asset.getStatus() != AssetStatus.AVAILABLE) {
            throw new IllegalStateException("Only available assets can be assigned.");
        }

        asset.setEmployeeId(employeeId);
        asset.setStatus(AssetStatus.ASSIGNED);

        AssetAssignment assignment = new AssetAssignment(
                nextAssignmentId++,
                assetId,
                employeeId,
                LocalDate.now(),
                null,
                AssignmentStatus.ACTIVE
        );
        assignments.add(assignment);
        return assignment;
    }

    public void returnAsset(int assetId) {
        if (assetDAO != null) {
            assetDAO.returnAsset(assetId);
            return;
        }
        Asset asset = findAssetById(assetId);
        if (asset == null) {
            throw new IllegalArgumentException("Asset not found.");
        }
        if (asset.getStatus() != AssetStatus.ASSIGNED) {
            throw new IllegalStateException("Only assigned assets can be returned.");
        }

        asset.setEmployeeId(0);
        asset.setStatus(AssetStatus.AVAILABLE);

        AssetAssignment activeAssignment = findActiveAssignment(assetId);
        if (activeAssignment != null) {
            activeAssignment.setReturnDate(LocalDate.now());
            activeAssignment.setStatus(AssignmentStatus.RETURNED);
        }
    }

    public List<Asset> getAllAssets() {
        if (assetDAO != null) {
            return assetDAO.getAllAssets();
        }
        return new ArrayList<>(assets);
    }

    public List<AssetAssignment> getAllAssignments() {
        if (assetDAO != null) {
            return assetDAO.getAllAssignments();
        }
        return new ArrayList<>(assignments);
    }

    private AssetAssignment findActiveAssignment(int assetId) {
        for (AssetAssignment assignment : assignments) {
            if (assignment.getAssetId() == assetId
                    && assignment.getStatus() == AssignmentStatus.ACTIVE) {
                return assignment;
            }
        }
        return null;
    }
}
