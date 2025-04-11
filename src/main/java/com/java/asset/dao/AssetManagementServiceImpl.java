package com.java.asset.dao;

import com.java.asset.model.Asset;
import com.java.asset.model.AssetAllocations;
import com.java.asset.model.AssetStatus;
import com.java.asset.model.MaintenanceRecord;
import com.java.asset.model.Reservations;
import com.java.asset.util.ConnectionHelper;
import com.java.asset.exception.AssetNotFound;
import com.java.asset.exception.AssetNotMaintain;
import com.java.asset.exception.EmployeeNotFound;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AssetManagementServiceImpl implements AssetManagementService {

    private Connection connection;
    private PreparedStatement pst;

    
    @Override
    public void validateEmployeeExists(int employeeId) throws ClassNotFoundException, SQLException, EmployeeNotFound {
        connection = ConnectionHelper.getConnection();
        String query = "SELECT * FROM employees WHERE employee_id = ?";
        pst = connection.prepareStatement(query);
        pst.setInt(1, employeeId);
        ResultSet rs = pst.executeQuery();

        if (!rs.next()) {
            throw new EmployeeNotFound("Employee with ID " + employeeId + " does not exist.");
        }
    }

    
    @Override
    public boolean addAsset(Asset asset) throws ClassNotFoundException, SQLException {
        connection = ConnectionHelper.getConnection();
        String cmd = "INSERT INTO assets(name, type, serial_number, purchase_date, location, status, owner_id) VALUES (?, ?, ?, ?, ?, ?, ?)";
        pst = connection.prepareStatement(cmd);
        pst.setString(1, asset.getName());
        pst.setString(2, asset.getType());
        pst.setString(3, asset.getSerialNumber());
        pst.setDate(4, new java.sql.Date(asset.getPurchaseDate().getTime()));
        pst.setString(5, asset.getLocation());
        pst.setString(6, asset.getStatus().name());
        pst.setInt(7, asset.getOwnerId());
        int rows = pst.executeUpdate();
        return rows > 0;
    }

    @Override
    public boolean updateAsset(Asset asset) throws ClassNotFoundException, SQLException, AssetNotFound {
        connection = ConnectionHelper.getConnection();
        String cmd = "UPDATE assets SET name=?, type=?, serial_number=?, purchase_date=?, location=?, status=?, owner_id=? WHERE asset_id=?";
        pst = connection.prepareStatement(cmd);
        pst.setString(1, asset.getName());
        pst.setString(2, asset.getType());
        pst.setString(3, asset.getSerialNumber());
        pst.setDate(4, new java.sql.Date(asset.getPurchaseDate().getTime()));
        pst.setString(5, asset.getLocation());
        pst.setString(6, asset.getStatus().name());
        pst.setInt(7, asset.getOwnerId());
        pst.setInt(8, asset.getAssetId());
        int rows = pst.executeUpdate();
        if (rows == 0) {
            throw new AssetNotFound("Asset with ID " + asset.getAssetId() + " not found.");
        }
        return true;
    }
    
    
    @Override
    public boolean deleteAsset(int assetId) throws ClassNotFoundException, SQLException, AssetNotFound {
        connection = ConnectionHelper.getConnection();

        // 1. Check if asset exists
        String checkQuery = "SELECT * FROM assets WHERE asset_id = ?";
        PreparedStatement checkStmt = connection.prepareStatement(checkQuery);
        checkStmt.setInt(1, assetId);
        ResultSet rs = checkStmt.executeQuery();
        if (!rs.next()) {
            throw new AssetNotFound("Asset with ID " + assetId + " not found.");
        }

        // 2. Check if currently allocated
        if (isAssetAllocated(assetId)) {
            int empId = getAllocatedEmployeeId(assetId);
            System.out.println("The asset is currently allocated to Employee ID: " + empId + ". Please deallocate it first.");
            return false;
        }

        // 3. Check if reserved
        if (isAssetReserved(assetId)) {
            int resId = getApprovedReservationId(assetId);
            System.out.println("Asset has an approved reservation (Reservation ID: " + resId + "). Please withdraw it first.");
            return false;
        }

        // 4. Delete asset
        String deleteQuery = "DELETE FROM assets WHERE asset_id = ?";
        PreparedStatement deleteStmt = connection.prepareStatement(deleteQuery);
        deleteStmt.setInt(1, assetId);
        int rows = deleteStmt.executeUpdate();
        return rows > 0;
    }

    // Utility method: check if asset is allocated
    @Override
    public boolean isAssetAllocated(int assetId) throws SQLException, ClassNotFoundException {
        connection = ConnectionHelper.getConnection();
        String query = "SELECT * FROM asset_allocations WHERE asset_id = ? AND return_date IS NULL";
        PreparedStatement stmt = connection.prepareStatement(query);
        stmt.setInt(1, assetId);
        ResultSet rs = stmt.executeQuery();
        return rs.next();
    }

    @Override
    public int getAllocatedEmployeeId(int assetId) throws SQLException, ClassNotFoundException {
        connection = ConnectionHelper.getConnection();
        String query = "SELECT employee_id FROM asset_allocations WHERE asset_id = ? AND return_date IS NULL";
        PreparedStatement stmt = connection.prepareStatement(query);
        stmt.setInt(1, assetId);
        ResultSet rs = stmt.executeQuery();
        if (rs.next()) {
            return rs.getInt("employee_id");
        }
        return -1;
    }

    @Override
    public boolean isAssetReserved(int assetId) throws SQLException, ClassNotFoundException {
        connection = ConnectionHelper.getConnection();
        String query = "SELECT * FROM reservations WHERE asset_id = ? AND status = 'Approved'";
        PreparedStatement stmt = connection.prepareStatement(query);
        stmt.setInt(1, assetId);
        ResultSet rs = stmt.executeQuery();
        return rs.next();
    }

    @Override
    public int getApprovedReservationId(int assetId) throws SQLException, ClassNotFoundException {
        connection = ConnectionHelper.getConnection();
        String query = "SELECT reservation_id FROM reservations WHERE asset_id = ? AND status = 'Approved'";
        PreparedStatement stmt = connection.prepareStatement(query);
        stmt.setInt(1, assetId);
        ResultSet rs = stmt.executeQuery();
        if (rs.next()) {
            return rs.getInt("reservation_id");
        }
        return -1;
    }
      

    @Override
    public List<Asset> showAllAssets() throws ClassNotFoundException, SQLException {
        connection = ConnectionHelper.getConnection();
        String cmd = "SELECT * FROM assets";
        pst = connection.prepareStatement(cmd);
        ResultSet rs = pst.executeQuery();
        List<Asset> assetList = new ArrayList<>();
        while (rs.next()) {
            Asset asset = new Asset();
            asset.setAssetId(rs.getInt("asset_id"));
            asset.setName(rs.getString("name"));
            asset.setType(rs.getString("type"));
            asset.setSerialNumber(rs.getString("serial_number"));
            asset.setPurchaseDate(rs.getDate("purchase_date"));
            asset.setLocation(rs.getString("location"));
            asset.setStatus(AssetStatus.valueOf(rs.getString("status")));
            asset.setOwnerId(rs.getInt("owner_id"));
            assetList.add(asset);
        }
        return assetList;
    }

    @Override
    public Asset searchAsset(int assetId) throws ClassNotFoundException, SQLException, AssetNotFound {
        connection = ConnectionHelper.getConnection();
        String cmd = "SELECT * FROM assets WHERE asset_id = ?";
        pst = connection.prepareStatement(cmd);
        pst.setInt(1, assetId);
        ResultSet rs = pst.executeQuery();
        if (rs.next()) {
            Asset asset = new Asset();
            asset.setAssetId(rs.getInt("asset_id"));
            asset.setName(rs.getString("name"));
            asset.setType(rs.getString("type"));
            asset.setSerialNumber(rs.getString("serial_number"));
            asset.setPurchaseDate(rs.getDate("purchase_date"));
            asset.setLocation(rs.getString("location"));
            asset.setStatus(AssetStatus.valueOf(rs.getString("status")));
            asset.setOwnerId(rs.getInt("owner_id"));
            return asset;
        } else {
            throw new AssetNotFound("Asset with ID " + assetId + " not found.");
        }
    }
    
    @Override
    public boolean allocateAsset(int assetId, int employeeId, String allocationDate) throws ClassNotFoundException, SQLException, AssetNotFound, AssetNotMaintain {
        
        connection = ConnectionHelper.getConnection();

        // Check if asset exists and retrieve status
        String checkCmd = "SELECT status FROM assets WHERE asset_id = ?";
        PreparedStatement checkStmt = connection.prepareStatement(checkCmd);
        checkStmt.setInt(1, assetId);
        ResultSet rs = checkStmt.executeQuery();

        if (!rs.next()) {
            throw new AssetNotFound("Asset with ID " + assetId + " not found.");
        }

        String status = rs.getString("status");

        //If asset is Decommissioned, throw exception or return false
        if ("Decommissioned".equalsIgnoreCase(status)) {
            System.out.println("Asset is decommissioned and cannot be allocated.");
            return false;
            // Alternatively: throw new IllegalStateException("Decommissioned asset cannot be allocated.");
        }
        else if ("In_Use".equalsIgnoreCase(status)) {
            System.out.println("Asset is already allocated to another employee.");
            return false;
        }
        
        
        // Check last maintenance date from maintenance_records
        String maintenanceQuery = "SELECT MAX(maintenance_date) AS last_date FROM maintenance_records WHERE asset_id = ?";
        PreparedStatement maintenanceStmt = connection.prepareStatement(maintenanceQuery);
        maintenanceStmt.setInt(1, assetId);
        ResultSet maintenanceRs = maintenanceStmt.executeQuery();

        if (maintenanceRs.next()) {
            Date lastDate = maintenanceRs.getDate("last_date");

            if (lastDate != null) {
                // Get current date
                java.util.Calendar calNow = java.util.Calendar.getInstance();
                int currentYear = calNow.get(java.util.Calendar.YEAR);

                // Get last maintenance year
                java.util.Calendar calLast = java.util.Calendar.getInstance();
                calLast.setTime(lastDate);
                int lastMaintenanceYear = calLast.get(java.util.Calendar.YEAR);

                int diffYears = currentYear - lastMaintenanceYear;

                // If maintenance is older than 2 years, throw exception
                if (diffYears >= 2) {
                    throw new AssetNotMaintain("Asset with ID " + assetId + " has not been maintained for over 2 years so cannot allocate");
                }
            }
        }

        // Convert allocation date
        Date sqlDate = Date.valueOf(allocationDate);  // Assumes "yyyy-MM-dd"
        
        
        // Check if there's an approved reservation for this asset on the allocation date
        String reservationCheckQuery = "SELECT * FROM reservations WHERE asset_id = ? AND status = 'Approved' AND ? BETWEEN start_date AND end_date";
        PreparedStatement reservationStmt = connection.prepareStatement(reservationCheckQuery);
        reservationStmt.setInt(1, assetId);
        reservationStmt.setDate(2, Date.valueOf(allocationDate));
        ResultSet reservationRs = reservationStmt.executeQuery();

        if (reservationRs.next()) {
            System.out.println("Asset has an approved reservation during this date. Allocation denied.");
            return false;
        }

        // Allocate asset
        String insertCmd = "INSERT INTO asset_allocations(asset_id, employee_id, allocation_date) VALUES (?, ?, ?)";
        PreparedStatement insertStmt = connection.prepareStatement(insertCmd);
        insertStmt.setInt(1, assetId);
        insertStmt.setInt(2, employeeId);
        insertStmt.setDate(3, sqlDate);

        int rows = insertStmt.executeUpdate();

        // If successful, update status
        if (rows > 0) {
            String statusUpdateCmd = "UPDATE assets SET status = 'In_Use' WHERE asset_id = ?";
            PreparedStatement statusStmt = connection.prepareStatement(statusUpdateCmd);
            statusStmt.setInt(1, assetId);
            int statusRows = statusStmt.executeUpdate();

            if (statusRows > 0) {
                System.out.println("Asset status updated to In_Use.");
            } else {
                System.out.println("Asset status update failed.");
            }

            return true;
        }

        return false;
    }


    @Override
    public List<AssetAllocations> showAssetAllocations() throws ClassNotFoundException, SQLException {
        connection = ConnectionHelper.getConnection();
        String cmd = "SELECT * FROM asset_allocations";
        pst = connection.prepareStatement(cmd);
        ResultSet rs = pst.executeQuery();

        List<AssetAllocations> allocationList = new ArrayList<>();

        while (rs.next()) {
            AssetAllocations allocation = new AssetAllocations();
            allocation.setAllocationId(rs.getInt("allocation_id"));
            allocation.setAssetId(rs.getInt("asset_id"));
            allocation.setEmployeeId(rs.getInt("employee_id"));
            allocation.setAllocationDate(rs.getDate("allocation_date"));
            allocation.setReturnDate(rs.getDate("return_date"));  // will be null if not yet returned
            allocationList.add(allocation);
        }

        return allocationList;
    }
    
    
    
    @Override
    public boolean deallocateAsset(int assetId, int employeeId, String returnDate)
            throws ClassNotFoundException, SQLException, AssetNotFound {

        connection = ConnectionHelper.getConnection();

        // Check if allocation exists and get the allocation_date
        String checkCmd = "SELECT allocation_date FROM asset_allocations WHERE asset_id = ? AND employee_id = ? AND return_date IS NULL";
        pst = connection.prepareStatement(checkCmd);
        pst.setInt(1, assetId);
        pst.setInt(2, employeeId);
        ResultSet rs = pst.executeQuery();

        if (!rs.next()) {
            throw new AssetNotFound("No active allocation found for asset ID " + assetId + " and employee ID " + employeeId);
        }

        Date allocationDate = rs.getDate("allocation_date");
        java.sql.Date sqlReturnDate = java.sql.Date.valueOf(returnDate);

        // Validate that return date is not before allocation date
        if (sqlReturnDate.before(allocationDate)) {
            System.out.println("Return date cannot be before allocation date.");
            return false;
        }

        // Update return_date
        String updateCmd = "UPDATE asset_allocations SET return_date = ? WHERE asset_id = ? AND employee_id = ? AND return_date IS NULL";
        pst = connection.prepareStatement(updateCmd);
        pst.setDate(1, sqlReturnDate);
        pst.setInt(2, assetId);
        pst.setInt(3, employeeId);

        int rows = pst.executeUpdate();

        if (rows > 0) {
            // Set asset status to Available
            String statusUpdateCmd = "UPDATE assets SET status = 'Available' WHERE asset_id = ?";
            pst = connection.prepareStatement(statusUpdateCmd);
            pst.setInt(1, assetId);
            pst.executeUpdate();

            return true;
        }

        return false;
    }


    @Override
    public boolean performMaintenance(int assetId, String maintenanceDate, String description, double cost)
            throws ClassNotFoundException, SQLException, AssetNotFound {

        connection = ConnectionHelper.getConnection();

        // 1. Check if asset exists
        String checkAssetQuery = "SELECT * FROM assets WHERE asset_id = ?";
        PreparedStatement checkStmt = connection.prepareStatement(checkAssetQuery);
        checkStmt.setInt(1, assetId);
        ResultSet rs = checkStmt.executeQuery();
        if (!rs.next()) {
            throw new AssetNotFound("Asset with ID " + assetId + " not found.");
        }

        // 2. Insert maintenance record
        String insertQuery = "INSERT INTO maintenance_records (asset_id, maintenance_date, description, cost) VALUES (?, ?, ?, ?)";
        PreparedStatement insertStmt = connection.prepareStatement(insertQuery);
        insertStmt.setInt(1, assetId);
        insertStmt.setDate(2, Date.valueOf(maintenanceDate));
        insertStmt.setString(3, description);
        insertStmt.setDouble(4, cost);

        int rowsInserted = insertStmt.executeUpdate();

        // 3. If inserted, update asset status to Under_Maintenance
        if (rowsInserted > 0) {
            String updateStatusQuery = "UPDATE assets SET status = 'Under_Maintenance' WHERE asset_id = ?";
            PreparedStatement statusStmt = connection.prepareStatement(updateStatusQuery);
            statusStmt.setInt(1, assetId);
            statusStmt.executeUpdate();
            return true;
        }

        return false;
    }


    @Override
    public List<MaintenanceRecord> showMaintenanceRecords() throws ClassNotFoundException, SQLException {
        connection = ConnectionHelper.getConnection();
        String cmd = "SELECT * FROM maintenance_records";
        pst = connection.prepareStatement(cmd);
        ResultSet rs = pst.executeQuery();

        List<MaintenanceRecord> records = new ArrayList<>();

        while (rs.next()) {
            MaintenanceRecord record = new MaintenanceRecord();
            record.setMaintenanceId(rs.getInt("maintenance_id"));
            record.setAssetId(rs.getInt("asset_id"));
            record.setMaintenanceDate(rs.getDate("maintenance_date"));
            record.setDescription(rs.getString("description"));
            record.setCost(rs.getDouble("cost"));
            records.add(record);
        }

        return records;
    }

    @Override
    public boolean reserveAsset(int assetId, int employeeId, String reservationDate, String startDate, String endDate)
            throws ClassNotFoundException, SQLException, AssetNotFound, AssetNotMaintain {

        connection = ConnectionHelper.getConnection();

        // 1. Check if asset exists
        String checkAssetQuery = "SELECT * FROM assets WHERE asset_id = ?";
        PreparedStatement assetStmt = connection.prepareStatement(checkAssetQuery);
        assetStmt.setInt(1, assetId);
        ResultSet assetRs = assetStmt.executeQuery();

        if (!assetRs.next()) {
            throw new AssetNotFound("Asset with ID " + assetId + " not found.");
        }

        String status = assetRs.getString("status");
        if ("Decommissioned".equalsIgnoreCase(status)) {
            System.out.println("Asset is decommissioned and cannot be reserved. Reservation canceled.");

            // Insert reservation as Canceled
            insertReservation(assetId, employeeId, reservationDate, startDate, endDate, "Canceled");
            return false;
        }

        // 2. Convert dates
        Date resDate = Date.valueOf(reservationDate);
        Date start = Date.valueOf(startDate);
        Date end = Date.valueOf(endDate);

        // 3. Check date logic
        if (!(resDate.before(start) && start.before(end))) {
            System.out.println("Invalid date sequence. Reservation date must be before start date, and start date before end date.");
            return false;
        }

        // 4. Check last maintenance date
        String maintenanceQuery = "SELECT MAX(maintenance_date) AS last_date FROM maintenance_records WHERE asset_id = ?";
        PreparedStatement maintenanceStmt = connection.prepareStatement(maintenanceQuery);
        maintenanceStmt.setInt(1, assetId);
        ResultSet maintenanceRs = maintenanceStmt.executeQuery();

        if (maintenanceRs.next()) {
            Date lastDate = maintenanceRs.getDate("last_date");

            if (lastDate != null) {
                java.util.Calendar calStart = java.util.Calendar.getInstance();
                calStart.setTime(start);
                int startYear = calStart.get(java.util.Calendar.YEAR);

                java.util.Calendar calLast = java.util.Calendar.getInstance();
                calLast.setTime(lastDate);
                int lastYear = calLast.get(java.util.Calendar.YEAR);

                int diffYears = startYear - lastYear;

                if (diffYears >= 2) {
                    System.out.println("Asset with ID " + assetId + " has not been maintained for over 2 years. Reservation canceled.");
                    insertReservation(assetId, employeeId, reservationDate, startDate, endDate, "Canceled");
                    return false;
                }
            }
        }

        // 5.a Check for existing pending reservation by the same employee (to clean it up before new one)
        String selfPendingQuery = "SELECT reservation_id FROM reservations WHERE asset_id = ? AND employee_id = ? AND status = 'Pending' "
                + "AND ((start_date <= ? AND end_date >= ?) OR (start_date <= ? AND end_date >= ?))";
        PreparedStatement selfPendingStmt = connection.prepareStatement(selfPendingQuery);
        selfPendingStmt.setInt(1, assetId);
        selfPendingStmt.setInt(2, employeeId);
        selfPendingStmt.setDate(3, start);
        selfPendingStmt.setDate(4, start);
        selfPendingStmt.setDate(5, end);
        selfPendingStmt.setDate(6, end);

        ResultSet selfPendingRs = selfPendingStmt.executeQuery();
        if (selfPendingRs.next()) {
            int oldReservationId = selfPendingRs.getInt("reservation_id");
            String deleteOld = "DELETE FROM reservations WHERE reservation_id = ?";
            PreparedStatement deleteStmt = connection.prepareStatement(deleteOld);
            deleteStmt.setInt(1, oldReservationId);
            deleteStmt.executeUpdate();

            System.out.println("Your previous pending reservation (Reservation ID: " + oldReservationId + ") was removed.");
        }

        // 5.b Now check for conflicts from other employees
        String conflictQuery = "SELECT * FROM reservations WHERE asset_id = ? AND employee_id != ? AND status != 'Canceled' "
                + "AND ((start_date <= ? AND end_date >= ?) OR (start_date <= ? AND end_date >= ?))";
        PreparedStatement conflictStmt = connection.prepareStatement(conflictQuery);
        conflictStmt.setInt(1, assetId);
        conflictStmt.setInt(2, employeeId);
        conflictStmt.setDate(3, start);
        conflictStmt.setDate(4, start);
        conflictStmt.setDate(5, end);
        conflictStmt.setDate(6, end);

        ResultSet conflictRs = conflictStmt.executeQuery();

        if (conflictRs.next()) {
            int existingReservationId = conflictRs.getInt("reservation_id");
            Date existingStart = conflictRs.getDate("start_date");
            Date existingEnd = conflictRs.getDate("end_date");

            System.out.println("Asset is already reserved for the given period.");
            System.out.println("Reservation pending. Conflicts with:");
            System.out.println("→ Reservation ID: " + existingReservationId);
            System.out.println("→ Reserved From: " + existingStart + " To: " + existingEnd);

            insertReservation(assetId, employeeId, reservationDate, startDate, endDate, "Pending");
            return false;
        }
        
        // 6. Check if asset is currently allocated and not yet returned
        String allocationQuery = "SELECT allocation_date FROM asset_allocations WHERE asset_id = ? AND return_date IS NULL";
        PreparedStatement allocationStmt = connection.prepareStatement(allocationQuery);
        allocationStmt.setInt(1, assetId);
        ResultSet allocationRs = allocationStmt.executeQuery();

        if (allocationRs.next()) {
            Date allocationDate = allocationRs.getDate("allocation_date");

            // If reservation start date is after or equal to allocation date and asset not returned, deny reservation
            if (!start.before(allocationDate)) {
                // Insert reservation as Canceled due to allocation conflict
                String cancelQuery = "INSERT INTO reservations (asset_id, employee_id, reservation_date, start_date, end_date, status) "
                        + "VALUES (?, ?, ?, ?, ?, 'Canceled')";
                PreparedStatement cancelStmt = connection.prepareStatement(cancelQuery);
                cancelStmt.setInt(1, assetId);
                cancelStmt.setInt(2, employeeId);
                cancelStmt.setDate(3, resDate);
                cancelStmt.setDate(4, start);
                cancelStmt.setDate(5, end);
                cancelStmt.executeUpdate();

                System.out.println("Asset is currently in use(allocated) and not yet returned. Reservation Canceled.");
                return false;
            }
        }

        // 7. Insert reservation as Approved
        insertReservation(assetId, employeeId, reservationDate, startDate, endDate, "Approved");
        System.out.println("Asset reserved successfully!");
        return true;
    }

    
    private void insertReservation(int assetId, int employeeId, String reservationDate, String startDate, String endDate, String status)
            throws SQLException {
        String insertQuery = "INSERT INTO reservations (asset_id, employee_id, reservation_date, start_date, end_date, status) VALUES (?, ?, ?, ?, ?, ?)";
        PreparedStatement insertStmt = connection.prepareStatement(insertQuery);
        insertStmt.setInt(1, assetId);
        insertStmt.setInt(2, employeeId);
        insertStmt.setDate(3, Date.valueOf(reservationDate));
        insertStmt.setDate(4, Date.valueOf(startDate));
        insertStmt.setDate(5, Date.valueOf(endDate));
        insertStmt.setString(6, status);
        insertStmt.executeUpdate();
    }

    
    @Override
    public boolean withdrawReservation(int reservationId) throws ClassNotFoundException, SQLException, AssetNotFound {
        connection = ConnectionHelper.getConnection();

        // 1. Check if reservation exists
        String checkQuery = "SELECT * FROM reservations WHERE reservation_id = ?";
        PreparedStatement checkStmt = connection.prepareStatement(checkQuery);
        checkStmt.setInt(1, reservationId);
        ResultSet rs = checkStmt.executeQuery();

        if (!rs.next()) {
            throw new AssetNotFound("Reservation with ID " + reservationId + " not found.");
        }

        String status = rs.getString("status");
        int assetId = rs.getInt("asset_id");

        // 2. If already canceled, no need to update again
        if ("Canceled".equalsIgnoreCase(status)) {
            System.out.println("Reservation is already canceled.");
            return false;
        }

        // 3. Cancel reservation
        String updateQuery = "UPDATE reservations SET status = 'Canceled' WHERE reservation_id = ?";
        PreparedStatement updateStmt = connection.prepareStatement(updateQuery);
        updateStmt.setInt(1, reservationId);
        int rowsUpdated = updateStmt.executeUpdate();

        if (rowsUpdated > 0) {
            // 4. Set asset status to Available (only if it's not currently allocated)
            String allocCheck = "SELECT * FROM asset_allocations WHERE asset_id = ? AND return_date IS NULL";
            PreparedStatement allocStmt = connection.prepareStatement(allocCheck);
            allocStmt.setInt(1, assetId);
            ResultSet allocRs = allocStmt.executeQuery();

            if (!allocRs.next()) {
                // If not currently allocated, make it available
                String statusUpdateCmd = "UPDATE assets SET status = 'Available' WHERE asset_id = ?";
                PreparedStatement statusStmt = connection.prepareStatement(statusUpdateCmd);
                statusStmt.setInt(1, assetId);
                statusStmt.executeUpdate();
            }

            return true;
        }

        return false;
    }

    @Override
    public List<Reservations> showAllReservations() throws ClassNotFoundException, SQLException {
        connection = ConnectionHelper.getConnection();
        String query = "SELECT * FROM reservations";
        pst = connection.prepareStatement(query);
        ResultSet rs = pst.executeQuery();

        List<Reservations> reservationList = new ArrayList<>();

        while (rs.next()) {
            Reservations reservation = new Reservations();
            reservation.setReservationId(rs.getInt("reservation_id"));
            reservation.setAssetId(rs.getInt("asset_id"));
            reservation.setEmployeeId(rs.getInt("employee_id"));
            reservation.setReservationDate(rs.getDate("reservation_date"));
            reservation.setStartDate(rs.getDate("start_date"));
            reservation.setEndDate(rs.getDate("end_date"));
            reservation.setStatus(rs.getString("status"));
            reservationList.add(reservation);
        }

        return reservationList;
    }

    
}
