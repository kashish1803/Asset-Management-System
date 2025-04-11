package com.java.asset;

import static org.junit.Assert.*;

import java.util.Date;

import org.junit.Test;

import com.java.asset.model.Asset;
import com.java.asset.model.AssetStatus;

public class AssetTest {

	@Test
    public void testDefaultConstructor() {
        Asset asset = new Asset();
        assertNotNull(asset);
    }

    @Test
    public void testParameterizedConstructor() {
        Date purchaseDate = new Date();
        Asset asset = new Asset(1, "Dell Laptop", "Laptop", "DL123456", purchaseDate, "Mumbai Office", AssetStatus.Available, 10);

        assertEquals(1, asset.getAssetId());
        assertEquals("Dell Laptop", asset.getName());
        assertEquals("Laptop", asset.getType());
        assertEquals("DL123456", asset.getSerialNumber());
        assertEquals(purchaseDate, asset.getPurchaseDate());
        assertEquals("Mumbai Office", asset.getLocation());
        assertEquals(AssetStatus.Available, asset.getStatus());
        assertEquals(10, asset.getOwnerId());
    }

    @Test
    public void testGettersAndSetters() {
        Asset asset = new Asset();
        Date date = new Date();

        asset.setAssetId(2);
        asset.setName("Canon Printer");
        asset.setType("Equipment");
        asset.setSerialNumber("CP202020");
        asset.setPurchaseDate(date);
        asset.setLocation("Delhi Office");
        asset.setStatus(AssetStatus.In_Use);
        asset.setOwnerId(5);

        assertEquals(2, asset.getAssetId());
        assertEquals("Canon Printer", asset.getName());
        assertEquals("Equipment", asset.getType());
        assertEquals("CP202020", asset.getSerialNumber());
        assertEquals(date, asset.getPurchaseDate());
        assertEquals("Delhi Office", asset.getLocation());
        assertEquals(AssetStatus.In_Use, asset.getStatus());
        assertEquals(5, asset.getOwnerId());
    }

    @Test
    public void testToString() {
        Date date = new Date();
        Asset asset = new Asset(3, "Samsung Monitor", "Monitor", "SM111111", date, "Chennai Office", AssetStatus.Under_Maintenance, 7);
        String expected = "Asset [assetId=3, name=Samsung Monitor, type=Monitor, serialNumber=SM111111, purchaseDate=" 
                          + date + ", location=Chennai Office, status=Under_Maintenance, ownerId=7]";
        assertEquals(expected, asset.toString());
    }

}
