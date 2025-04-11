package com.java.asset.model;

public class Employee {
    private int employeeId;
    private String name;
    private String department;
    private String email;
    private String password;
	public int getEmployeeId() {
		return employeeId;
	}
	public void setEmployeeId(int employeeId) {
		this.employeeId = employeeId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDepartment() {
		return department;
	}
	public void setDepartment(String department) {
		this.department = department;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	
	
	public Employee() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	public Employee(int employeeId, String name, String department, String email, String password) {
		super();
		this.employeeId = employeeId;
		this.name = name;
		this.department = department;
		this.email = email;
		this.password = password;
	}
	
	
	@Override
	public String toString() {
		return "Employee [employeeId=" + employeeId + ", name=" + name + ", department=" + department + ", email="
				+ email + ", password=" + password + "]";
	}
	
    
    // Getters and Setters
    // toString()
}
