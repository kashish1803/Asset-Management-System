package com.java.asset;

import static org.junit.Assert.*;

import java.sql.Date;

import org.junit.Test;

import com.java.asset.model.AssetAllocations;

public class AssetAllocationsTest {

	    @Test
	    public void testToString() {
	    	Date allocDate = Date.valueOf("2023-04-05");
	        Date returnDate = Date.valueOf("2023-04-20");

	        AssetAllocations allocation = new AssetAllocations(10, 101, 201, allocDate, returnDate);

	        String expected = "AssetAllocations [allocationId=10, assetId=101, employeeId=201"
	                + ", allocationDate=" + allocDate
	                + ", returnDate=" + returnDate + "]";

	        assertEquals(expected, allocation.toString());
	    }

	    @Test
	    public void testGettersAndSetters() {
	        AssetAllocations allocation = new AssetAllocations();

	        Date allocDate = Date.valueOf("2023-03-01");
	        Date returnDate = Date.valueOf("2023-03-15");


	        allocation.setAllocationId(1);
	        allocation.setAssetId(100);
	        allocation.setEmployeeId(200);
	        allocation.setAllocationDate(allocDate);
	        allocation.setReturnDate(returnDate);

	        assertEquals(1, allocation.getAllocationId());
	        assertEquals(100, allocation.getAssetId());
	        assertEquals(200, allocation.getEmployeeId());
	        assertEquals(allocDate, allocation.getAllocationDate());
	        assertEquals(returnDate, allocation.getReturnDate());
	    }

	    @Test
	    public void testConstructors() {
	    	Date allocDate = Date.valueOf("2023-05-10");
	        Date returnDate = Date.valueOf("2023-06-01");


	        AssetAllocations allocation = new AssetAllocations(2, 202, 302, allocDate, returnDate);

	        assertEquals(2, allocation.getAllocationId());
	        assertEquals(202, allocation.getAssetId());
	        assertEquals(302, allocation.getEmployeeId());
	        assertEquals(allocDate, allocation.getAllocationDate());
	        assertEquals(returnDate, allocation.getReturnDate());

	        AssetAllocations defaultAlloc = new AssetAllocations();
	        assertNotNull(defaultAlloc);
	    }
	


}
