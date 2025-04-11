package com.java.asset.dao;


import com.java.asset.exception.AssetNotFound;
import com.java.asset.exception.AssetNotMaintain;
import com.java.asset.exception.EmployeeNotFound;
import com.java.asset.model.Asset;
import com.java.asset.model.AssetAllocations;
import com.java.asset.model.MaintenanceRecord;
import com.java.asset.model.Reservations;

import java.sql.SQLException;

import java.util.List;

public interface AssetManagementService {
	
	void validateEmployeeExists(int employeeId) throws ClassNotFoundException, SQLException, EmployeeNotFound;

	boolean addAsset(Asset asset) throws ClassNotFoundException, SQLException;

    boolean updateAsset(Asset asset) throws ClassNotFoundException, SQLException, AssetNotFound;

    boolean deleteAsset(int assetId) throws ClassNotFoundException, SQLException, AssetNotFound;

    List<Asset> showAllAssets() throws ClassNotFoundException, SQLException;

    Asset searchAsset(int assetId) throws ClassNotFoundException, SQLException, AssetNotFound;
    
    List<AssetAllocations> showAssetAllocations() throws ClassNotFoundException, SQLException;

    boolean allocateAsset(int assetId, int employeeId, String allocationDate) throws ClassNotFoundException, SQLException, AssetNotFound, AssetNotMaintain;

    boolean deallocateAsset(int assetId, int employeeId, String returnDate) throws ClassNotFoundException, SQLException, AssetNotFound;

    boolean performMaintenance(int assetId, String maintenanceDate, String description, double cost) throws ClassNotFoundException, SQLException, AssetNotFound;

    List<MaintenanceRecord> showMaintenanceRecords() throws ClassNotFoundException, SQLException;

    boolean reserveAsset(int assetId, int employeeId, String reservationDate, String startDate, String endDate) throws ClassNotFoundException, SQLException, AssetNotFound, AssetNotMaintain;

    boolean withdrawReservation(int reservationId) throws ClassNotFoundException, SQLException, AssetNotFound;

    List<Reservations> showAllReservations() throws ClassNotFoundException, SQLException;


}

