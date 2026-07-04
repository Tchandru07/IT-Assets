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
import model.Asset;
import model.AssetAssignment;
import model.AssetStatus;
import model.AssignmentStatus;

public class AssetDAO {

    public void addAsset(Asset asset) {
        String sql = "INSERT INTO assets (asset_id, asset_name, asset_type, serial_number, employee_id, status) "
                + "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, asset.getAssetId());
            statement.setString(2, asset.getAssetName());
            statement.setString(3, asset.getAssetType());
            statement.setString(4, asset.getSerialNumber());
            if (asset.getEmployeeId() == 0) {
                statement.setNull(5, java.sql.Types.INTEGER);
            } else {
                statement.setInt(5, asset.getEmployeeId());
            }
            statement.setString(6, asset.getStatus().name());
            statement.executeUpdate();
        } catch (SQLException exception) {
            throw new IllegalStateException("Unable to add asset.", exception);
        }
    }

    public Asset findAssetById(int assetId) {
        String sql = "SELECT asset_id, asset_name, asset_type, serial_number, employee_id, status "
                + "FROM assets WHERE asset_id = ?";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, assetId);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return mapAsset(resultSet);
                }
            }
        } catch (SQLException exception) {
            throw new IllegalStateException("Unable to find asset.", exception);
        }

        return null;
    }

    public AssetAssignment assignAsset(int assetId, int employeeId) {
        String assetSql = "SELECT status FROM assets WHERE asset_id = ? FOR UPDATE";
        String insertSql = "INSERT INTO asset_assignments "
                + "(asset_id, employee_id, assigned_date, return_date, status) VALUES (?, ?, ?, NULL, ?)";
        String updateSql = "UPDATE assets SET employee_id = ?, status = ? WHERE asset_id = ?";

        try (Connection connection = DBConnection.getConnection()) {
            connection.setAutoCommit(false);

            try {
                String status = readAssetStatusForUpdate(connection, assetSql, assetId);
                if (status == null) {
                    throw new IllegalArgumentException("Asset not found.");
                }
                if (AssetStatus.valueOf(status) != AssetStatus.AVAILABLE) {
                    throw new IllegalStateException("Only available assets can be assigned.");
                }

                int assignmentId;
                LocalDate assignedDate = LocalDate.now();
                try (PreparedStatement statement = connection.prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS)) {
                    statement.setInt(1, assetId);
                    statement.setInt(2, employeeId);
                    statement.setDate(3, Date.valueOf(assignedDate));
                    statement.setString(4, AssignmentStatus.ACTIVE.name());
                    statement.executeUpdate();

                    try (ResultSet keys = statement.getGeneratedKeys()) {
                        if (!keys.next()) {
                            throw new IllegalStateException("Assignment ID was not generated.");
                        }
                        assignmentId = keys.getInt(1);
                    }
                }

                try (PreparedStatement statement = connection.prepareStatement(updateSql)) {
                    statement.setInt(1, employeeId);
                    statement.setString(2, AssetStatus.ASSIGNED.name());
                    statement.setInt(3, assetId);
                    statement.executeUpdate();
                }

                connection.commit();
                return new AssetAssignment(
                        assignmentId,
                        assetId,
                        employeeId,
                        assignedDate,
                        null,
                        AssignmentStatus.ACTIVE
                );
            } catch (RuntimeException | SQLException exception) {
                connection.rollback();
                throw exception;
            } finally {
                connection.setAutoCommit(true);
            }
        } catch (SQLException exception) {
            throw new IllegalStateException("Unable to assign asset.", exception);
        }
    }

    public void returnAsset(int assetId) {
        String assetSql = "SELECT status FROM assets WHERE asset_id = ? FOR UPDATE";
        String assignmentSql = "UPDATE asset_assignments SET return_date = ?, status = ? "
                + "WHERE asset_id = ? AND status = ?";
        String assetUpdateSql = "UPDATE assets SET employee_id = NULL, status = ? WHERE asset_id = ?";

        try (Connection connection = DBConnection.getConnection()) {
            connection.setAutoCommit(false);

            try {
                String status = readAssetStatusForUpdate(connection, assetSql, assetId);
                if (status == null) {
                    throw new IllegalArgumentException("Asset not found.");
                }
                if (AssetStatus.valueOf(status) != AssetStatus.ASSIGNED) {
                    throw new IllegalStateException("Only assigned assets can be returned.");
                }

                LocalDate returnDate = LocalDate.now();
                try (PreparedStatement statement = connection.prepareStatement(assignmentSql)) {
                    statement.setDate(1, Date.valueOf(returnDate));
                    statement.setString(2, AssignmentStatus.RETURNED.name());
                    statement.setInt(3, assetId);
                    statement.setString(4, AssignmentStatus.ACTIVE.name());
                    statement.executeUpdate();
                }

                try (PreparedStatement statement = connection.prepareStatement(assetUpdateSql)) {
                    statement.setString(1, AssetStatus.AVAILABLE.name());
                    statement.setInt(2, assetId);
                    statement.executeUpdate();
                }

                connection.commit();
            } catch (RuntimeException | SQLException exception) {
                connection.rollback();
                throw exception;
            } finally {
                connection.setAutoCommit(true);
            }
        } catch (SQLException exception) {
            throw new IllegalStateException("Unable to return asset.", exception);
        }
    }

    public List<Asset> getAllAssets() {
        String sql = "SELECT asset_id, asset_name, asset_type, serial_number, employee_id, status "
                + "FROM assets ORDER BY asset_id";
        List<Asset> assets = new ArrayList<>();

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                assets.add(mapAsset(resultSet));
            }
        } catch (SQLException exception) {
            throw new IllegalStateException("Unable to list assets.", exception);
        }

        return assets;
    }

    public List<AssetAssignment> getAllAssignments() {
        String sql = "SELECT assignment_id, asset_id, employee_id, assigned_date, return_date, status "
                + "FROM asset_assignments ORDER BY assignment_id";
        List<AssetAssignment> assignments = new ArrayList<>();

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                assignments.add(mapAssignment(resultSet));
            }
        } catch (SQLException exception) {
            throw new IllegalStateException("Unable to list assignments.", exception);
        }

        return assignments;
    }

    private String readAssetStatusForUpdate(Connection connection, String sql, int assetId) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, assetId);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getString("status");
                }
            }
        }
        return null;
    }

    private Asset mapAsset(ResultSet resultSet) throws SQLException {
        int employeeId = resultSet.getInt("employee_id");
        if (resultSet.wasNull()) {
            employeeId = 0;
        }

        return new Asset(
                resultSet.getInt("asset_id"),
                resultSet.getString("asset_name"),
                resultSet.getString("asset_type"),
                resultSet.getString("serial_number"),
                employeeId,
                AssetStatus.valueOf(resultSet.getString("status"))
        );
    }

    private AssetAssignment mapAssignment(ResultSet resultSet) throws SQLException {
        Date returnDate = resultSet.getDate("return_date");
        return new AssetAssignment(
                resultSet.getInt("assignment_id"),
                resultSet.getInt("asset_id"),
                resultSet.getInt("employee_id"),
                resultSet.getDate("assigned_date").toLocalDate(),
                returnDate == null ? null : returnDate.toLocalDate(),
                AssignmentStatus.valueOf(resultSet.getString("status"))
        );
    }
}
