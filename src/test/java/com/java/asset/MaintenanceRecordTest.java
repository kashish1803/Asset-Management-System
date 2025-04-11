package com.java.asset;

import static org.junit.Assert.*;


import java.sql.Date;
import org.junit.Test;

import com.java.asset.model.MaintenanceRecord;

public class MaintenanceRecordTest {

	    @Test
	    public void testDefaultConstructor() {
	        MaintenanceRecord record = new MaintenanceRecord();
	        assertNotNull(record);
	    }

	    @Test
	    public void testParameterizedConstructor() {
	    	Date date = Date.valueOf("2024-12-01");
	        MaintenanceRecord record = new MaintenanceRecord(1, 1001, date, "Battery Replacement", 1500.50);

	        assertEquals(1, record.getMaintenanceId());
	        assertEquals(1001, record.getAssetId());
	        assertEquals(date, record.getMaintenanceDate());
	        assertEquals("Battery Replacement", record.getDescription());
	        assertEquals(1500.50, record.getCost(), 0.01);
	    }

	    @Test
	    public void testGettersAndSetters() {
	        MaintenanceRecord record = new MaintenanceRecord();
	        Date maintenanceDate = Date.valueOf("2024-10-10");

	        record.setMaintenanceId(2);
	        record.setAssetId(2002);
	        record.setMaintenanceDate(maintenanceDate);
	        record.setDescription("Oil Change");
	        record.setCost(750.25);

	        assertEquals(2, record.getMaintenanceId());
	        assertEquals(2002, record.getAssetId());
	        assertEquals(maintenanceDate, record.getMaintenanceDate());
	        assertEquals("Oil Change", record.getDescription());
	        assertEquals(750.25, record.getCost(), 0.01);
	    }

	    @Test
	    public void testToString() {
	        Date date = Date.valueOf("2025-01-01");

	        MaintenanceRecord record = new MaintenanceRecord(3, 3003, date, "Lens Replacement", 2200.00);
	        String expected = "MaintenanceRecord [maintenanceId=3, assetId=3003, maintenanceDate=" + date +
	                          ", description=Lens Replacement, cost=2200.0]";
	        assertEquals(expected, record.toString());
	    }

	


}
