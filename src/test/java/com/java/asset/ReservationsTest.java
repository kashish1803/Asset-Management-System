package com.java.asset;

import static org.junit.Assert.*;

import java.sql.Date;

import org.junit.Test;

import com.java.asset.model.Reservations;

public class ReservationsTest {



	    @Test
	    public void testToString() {
	    	Date reservationDate = Date.valueOf("2025-05-01");
	        Date startDate = Date.valueOf("2025-05-03");
	        Date endDate = Date.valueOf("2025-05-07");

	        Reservations reservation = new Reservations(101, 12, 33, reservationDate, startDate, endDate, "Approved");

	        String expected = "Reservations [reservationId=101, assetId=12, employeeId=33"
	                + ", reservationDate=" + reservationDate
	                + ", startDate=" + startDate
	                + ", endDate=" + endDate
	                + ", status=Approved]";

	        assertEquals(expected, reservation.toString());
	    }

	    @Test
	    public void testGettersAndSetters() {
	    	Date resDate = Date.valueOf("2025-04-01");
	        Date start = Date.valueOf("2025-04-05");
	        Date end = Date.valueOf("2025-04-10");

	        Reservations reservation = new Reservations();

	        reservation.setReservationId(5);
	        reservation.setAssetId(100);
	        reservation.setEmployeeId(200);
	        reservation.setReservationDate(resDate);
	        reservation.setStartDate(start);
	        reservation.setEndDate(end);
	        reservation.setStatus("Pending");

	        assertEquals(5, reservation.getReservationId());
	        assertEquals(100, reservation.getAssetId());
	        assertEquals(200, reservation.getEmployeeId());
	        assertEquals(resDate, reservation.getReservationDate());
	        assertEquals(start, reservation.getStartDate());
	        assertEquals(end, reservation.getEndDate());
	        assertEquals("Pending", reservation.getStatus());
	    }

	    @Test
	    public void testConstructors() {
	    	Date reservationDate = Date.valueOf("2025-03-01");
	        Date start = Date.valueOf("2025-03-05");
	        Date end = Date.valueOf("2025-03-08");

	        Reservations res = new Reservations(77, 11, 99, reservationDate, start, end, "Canceled");

	        assertEquals(77, res.getReservationId());
	        assertEquals(11, res.getAssetId());
	        assertEquals(99, res.getEmployeeId());
	        assertEquals(reservationDate, res.getReservationDate());
	        assertEquals(start, res.getStartDate());
	        assertEquals(end, res.getEndDate());
	        assertEquals("Canceled", res.getStatus());

	        Reservations defaultRes = new Reservations();
	        assertNotNull(defaultRes);
	    }


}
