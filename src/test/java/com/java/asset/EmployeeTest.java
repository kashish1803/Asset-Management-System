package com.java.asset;

import static org.junit.Assert.*;

import org.junit.Test;

import com.java.asset.model.Employee;

public class EmployeeTest {

	    @Test
	    public void testDefaultConstructor() {
	        Employee emp = new Employee();
	        assertNotNull(emp);
	    }

	    @Test
	    public void testParameterizedConstructor() {
	        Employee emp = new Employee(101, "Riya", "HR", "riya.hr@gmail.com", "pass123");

	        assertEquals(101, emp.getEmployeeId());
	        assertEquals("Riya", emp.getName());
	        assertEquals("HR", emp.getDepartment());
	        assertEquals("riya.hr@gmail.com", emp.getEmail());
	        assertEquals("pass123", emp.getPassword());
	    }

	    @Test
	    public void testGettersAndSetters() {
	        Employee emp = new Employee();

	        emp.setEmployeeId(102);
	        emp.setName("Rohan");
	        emp.setDepartment("Finance");
	        emp.setEmail("rohan.finance@gmail.com");
	        emp.setPassword("secure456");

	        assertEquals(102, emp.getEmployeeId());
	        assertEquals("Rohan", emp.getName());
	        assertEquals("Finance", emp.getDepartment());
	        assertEquals("rohan.finance@gmail.com", emp.getEmail());
	        assertEquals("secure456", emp.getPassword());
	    }

	    @Test
	    public void testToString() {
	        Employee emp = new Employee(103, "Sneha", "IT", "sneha.it@gmail.com", "admin789");
	        String expected = "Employee [employeeId=103, name=Sneha, department=IT, email=sneha.it@gmail.com, password=admin789]";
	        assertEquals(expected, emp.toString());
	    }
	
}
