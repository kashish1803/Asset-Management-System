package com.java.asset.main;


import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Scanner;

import com.java.asset.dao.AssetManagementService;
import com.java.asset.dao.AssetManagementServiceImpl;
import com.java.asset.exception.AssetNotFound;
import com.java.asset.exception.AssetNotMaintain;
import com.java.asset.exception.EmployeeNotFound;
import com.java.asset.model.Asset;
import com.java.asset.model.AssetAllocations;
import com.java.asset.model.AssetStatus;
import com.java.asset.model.MaintenanceRecord;
import com.java.asset.model.Reservations;
import com.java.asset.util.ConnectionHelper;

public class AssetManagementApp {

	static Scanner sc;
	static AssetManagementService service;
	static SimpleDateFormat sdf;

	static {
		sc = new Scanner(System.in);
		service = new AssetManagementServiceImpl();
		sdf = new SimpleDateFormat("yyyy-MM-dd");
	}

	public static void main(String[] args) throws AssetNotFound, AssetNotMaintain {
		int choice;
		do {
			System.out.println("\nA S S E T   M A N A G E M E N T");
			System.out.println("==================================");
			System.out.println("1. Add Asset");
			System.out.println("2. Update Asset");
			System.out.println("3. Delete Asset");
			System.out.println("4. Show All Assets");
			System.out.println("5. Search an asset");
			System.out.println("6. Allocate Asset");
			System.out.println("7. Show Asset Allocations");
			System.out.println("8. Deallocate Asset");
			System.out.println("9. Perform maintenance of an asset");
			System.out.println("10. Show All Maintenance records");
			System.out.println("11. Reserve Asset");
			System.out.println("12. Withdraw Reservation");
			System.out.println("13. Show All Reservations");
			System.out.println("14. Exit");
			System.out.println("==================================");
			System.out.print("Enter your choice: ");
			choice = sc.nextInt();
			sc.nextLine(); // Consume newline

			switch (choice) {
			case 1:
				addAssetMain();
				break;
			case 2:
				updateAssetMain();
				break;
			case 3:
				deleteAssetMain();
				break;
			case 4:
				showAllAssetsMain();
				break;
			case 5:
			    searchAssetMain();
			    break;
			case 6:
				allocateAssetMain();
				break;
			case 7:
				showAssetAllocationsMain();
				break;
			case 8:
			    deallocateAssetMain();
			    break;
			case 9:
			    performMaintenanceMain();
			    break;
			case 10:
			    showAllMaintenanceRecordsMain();
			    break;
			case 11:
			    reserveAssetMain();
			    break;
			case 12:
			    withdrawReservationMain();
			    break;
			case 13:
			    showAllReservationsMain();
			    break;
			case 14:
			    System.out.println("Exiting program...");
			    break;
			default:
				System.out.println("Invalid choice. Please try again.");
			}
		} while (choice != 14);
	}

	public static void addAssetMain() {
		try {
			Asset asset = new Asset();
			System.out.print("Enter name: ");
			asset.setName(sc.nextLine());
			System.out.print("Enter type: ");
			asset.setType(sc.nextLine());
			System.out.print("Enter serial number: ");
			asset.setSerialNumber(sc.nextLine());
			System.out.print("Enter purchase date (yyyy-MM-dd): ");
			asset.setPurchaseDate(sdf.parse(sc.nextLine()));
			System.out.print("Enter location: ");
			asset.setLocation(sc.nextLine());
			System.out.print("Enter status (In_Use/Decommissioned/Under_Maintenance/Available): ");
			String statusInput = sc.nextLine().trim();
			try {
		        asset.setStatus(AssetStatus.valueOf(statusInput));
		    } catch (IllegalArgumentException e) {
		        System.out.println("Invalid status. Please enter one of: In_Use, Decommissioned, Under_Maintenance, Available.");
		        return;
		    }
			System.out.print("Enter owner ID: ");
			asset.setOwnerId(sc.nextInt());
			sc.nextLine(); // Consume newline

			boolean result = service.addAsset(asset);
			if (result) {
			    System.out.println("Asset added successfully!");
			} else {
			    System.out.println("Failed to add asset.");
			}
		} catch (ParseException | ClassNotFoundException | SQLException e) {
			System.out.println("Error adding asset: " + e.getMessage());
		}
	}

	public static void updateAssetMain() throws AssetNotFound {
	    try {
	        System.out.print("Enter Asset ID to update: ");
	        int assetId = sc.nextInt();
	        sc.nextLine(); // Consume newline

	        // Check if asset exists
	        service.searchAsset(assetId);

	        // Proceed to take update details
	        Asset asset = new Asset();
	        asset.setAssetId(assetId);
	        System.out.print("Enter new name: ");
	        asset.setName(sc.nextLine());
	        System.out.print("Enter new type: ");
	        asset.setType(sc.nextLine());
	        System.out.print("Enter new serial number: ");
	        asset.setSerialNumber(sc.nextLine());
	        System.out.print("Enter new purchase date (yyyy-MM-dd): ");
	        asset.setPurchaseDate(sdf.parse(sc.nextLine()));
	        System.out.print("Enter new location: ");
	        asset.setLocation(sc.nextLine());
	        System.out.print("Enter new status (In_Use/Decommissioned/Under_Maintenance/Available): ");
	        String statusInput = sc.nextLine().trim();
	        try {
	            asset.setStatus(AssetStatus.valueOf(statusInput));
	        } catch (IllegalArgumentException e) {
	            System.out.println("Invalid status. Please enter one of: In_Use, Decommissioned, Under_Maintenance, Available.");
	            return;
	        }
	        System.out.print("Enter new owner ID: ");
	        asset.setOwnerId(sc.nextInt());
	        sc.nextLine(); // Consume newline

	        boolean updated = service.updateAsset(asset);
	        if (updated) {
	            System.out.println("Asset updated successfully!");
	        } else {
	            System.out.println("Asset update failed.");
	        }
	    } catch (AssetNotFound e) {
	        System.out.println("Asset not found: " + e.getMessage());
	    } catch (ParseException | ClassNotFoundException | SQLException e) {
	        System.out.println("Error updating asset: " + e.getMessage());
	    }
	}

	public static void deleteAssetMain() {
	    try {
	        System.out.print("Enter Asset ID to delete: ");
	        int assetId = sc.nextInt();
	        sc.nextLine(); // consume newline
	        
	        // Check if asset exists
	        service.searchAsset(assetId);

	        // Check if the asset is allocated
	        Connection conn = ConnectionHelper.getConnection();
	        String allocQuery = "SELECT employee_id, allocation_date FROM asset_allocations WHERE asset_id = ? AND return_date IS NULL";
	        PreparedStatement allocStmt = conn.prepareStatement(allocQuery);
	        allocStmt.setInt(1, assetId);
	        ResultSet allocRs = allocStmt.executeQuery();

	        if (allocRs.next()) {
	            int empId = allocRs.getInt("employee_id");
	            Date allocDate = allocRs.getDate("allocation_date");

	            System.out.println("The asset is currently allocated to Employee ID: " + empId + " since " + allocDate);
	            System.out.print("Do you want to deallocate it first? (YES/NO): ");
	            String answer = sc.nextLine().trim();

	            if (answer.equalsIgnoreCase("YES")) {
	                System.out.print("Enter Return Date (yyyy-MM-dd): ");
	                String returnDate = sc.nextLine();
	                boolean deallocated = service.deallocateAsset(assetId, empId, returnDate);
	                if (deallocated) {
	                    System.out.println("Asset successfully deallocated. Proceeding with deletion...");
	                } else {
	                    System.out.println("Deallocation failed. Cannot proceed with deletion.");
	                    return;
	                }
	            } else {
	                System.out.println("Cannot delete while asset is in use.");
	                return;
	            }
	        }

	        // Check if the asset has an approved reservation
	        String resQuery = "SELECT reservation_id FROM reservations WHERE asset_id = ? AND status = 'Approved'";
	        PreparedStatement resStmt = conn.prepareStatement(resQuery);
	        resStmt.setInt(1, assetId);
	        ResultSet resRs = resStmt.executeQuery();

	        if (resRs.next()) {
	            int resId = resRs.getInt("reservation_id");

	            System.out.println("Asset has an approved reservation (Reservation ID: " + resId + ").");
	            System.out.print("Do you want to withdraw the reservation first? (YES/NO): ");
	            String ans = sc.nextLine().trim();

	            if (ans.equalsIgnoreCase("YES")) {
	                boolean withdrawn = service.withdrawReservation(resId);
	                if (withdrawn) {
	                    System.out.println("Reservation withdrawn. Proceeding with deletion...");
	                } else {
	                    System.out.println("Failed to withdraw reservation. Cannot proceed with deletion.");
	                    return;
	                }
	            } else {
	                System.out.println("Cannot delete while reservation is active.");
	                return;
	            }
	        }

	        // Finally delete the asset
	        boolean deleted = service.deleteAsset(assetId);
	        if (deleted) {
	            System.out.println("Asset deleted successfully.");
	        } else {
	            System.out.println("Failed to delete asset.");
	        }

	    } catch (AssetNotFound e) {
	        System.out.println("Asset not found: " + e.getMessage());
	    } catch (Exception e) {
	        System.out.println("Error: " + e.getMessage());
	    }
	}


	public static void showAllAssetsMain() {
		try {
			List<Asset> assetList = service.showAllAssets();
			if (assetList.isEmpty()) {
				System.out.println("No assets found.");
			} else {
				for (Asset asset : assetList) {
					System.out.println(asset);
				}
			}
		} catch (ClassNotFoundException | SQLException e) {
			System.out.println("Error fetching asset list: " + e.getMessage());
		}
	}
	
	public static void searchAssetMain() throws AssetNotFound {
	    try {
	        System.out.print("Enter Asset ID to search: ");
	        int id = sc.nextInt();
	        sc.nextLine(); // Consume newline

	        Asset asset = service.searchAsset(id);
	        System.out.println("Asset Found:");
	        System.out.println(asset);
	    } catch (AssetNotFound e) {
	        System.out.println("Asset not found: " + e.getMessage());
	    } catch (ClassNotFoundException | SQLException e) {
	        System.out.println("Error searching asset: " + e.getMessage());
	    }
	}
	
	public static void allocateAssetMain() throws AssetNotFound, AssetNotMaintain {
	    try {
	        System.out.print("Enter Asset ID: ");
	        int assetId = sc.nextInt();

	        // Check if asset exists
	        service.searchAsset(assetId);

	        System.out.print("Enter Employee ID: ");
	        int employeeId = sc.nextInt();
	        service.validateEmployeeExists(employeeId);
	        sc.nextLine(); // Consume newline

	        System.out.print("Enter Allocation Date (yyyy-MM-dd): ");
	        String allocationDate = sc.nextLine();

	        boolean allocated = service.allocateAsset(assetId, employeeId, allocationDate);

	        if (allocated) {
	            System.out.println("Asset allocated successfully!");
	        } else {
	            System.out.println("Failed to allocate asset.");
	        }
	    } catch (EmployeeNotFound e) {
	        System.out.println("Error: " + e.getMessage());
	        return;
	    }catch (ClassNotFoundException | SQLException e) {
	        System.out.println("Error during allocation: " + e.getMessage());
	    }
	}


	public static void showAssetAllocationsMain() {
		try {
			List<AssetAllocations> allocations = service.showAssetAllocations();
			if (allocations.isEmpty()) {
				System.out.println("No allocations found.");
			} else {
				for (AssetAllocations alloc : allocations) {
					System.out.println("Allocation ID: " + alloc.getAllocationId());
					System.out.println("Asset ID: " + alloc.getAssetId());
					System.out.println("Employee ID: " + alloc.getEmployeeId());
					System.out.println("Allocation Date: " + alloc.getAllocationDate());
					System.out.println("Return Date: " + alloc.getReturnDate());
					System.out.println("-------------------------------");
				}
			}
		} catch (ClassNotFoundException | SQLException e) {
			System.out.println("Error showing asset allocations: " + e.getMessage());
		}
	}
	
	public static void deallocateAssetMain() {
	    try {
	        System.out.print("Enter Allocation ID: ");
	        int allocationId = sc.nextInt();
	        sc.nextLine(); // consume newline

	        // Get assetId and employeeId using allocationId
	        Connection conn = ConnectionHelper.getConnection();
	        String query = "SELECT asset_id, employee_id FROM asset_allocations WHERE allocation_id = ? AND return_date IS NULL";
	        PreparedStatement pst = conn.prepareStatement(query);
	        pst.setInt(1, allocationId);
	        ResultSet rs = pst.executeQuery();

	        if (!rs.next()) {
	            System.out.println("No active allocation found with this ID.");
	            return;
	        }

	        int assetId = rs.getInt("asset_id");
	        int employeeId = rs.getInt("employee_id");

	        System.out.print("Enter Return Date (yyyy-MM-dd): ");
	        String returnDate = sc.nextLine();

	        boolean result = service.deallocateAsset(assetId, employeeId, returnDate);
	        if (result) {
	            System.out.println("Asset deallocated successfully!");
	        } else {
	            System.out.println("Failed to deallocate asset.");
	        }

	    } catch (AssetNotFound e) {
	        System.out.println("Asset not found: " + e.getMessage());
	    } catch (Exception e) {
	        System.out.println("Error during deallocation: " + e.getMessage());
	    }
	}



	public static void performMaintenanceMain() {
	    try {
	        System.out.print("Enter Asset ID: ");
	        int assetId = sc.nextInt();
	        sc.nextLine(); // consume newline

	        // Check if asset exists
	        service.searchAsset(assetId);

	        // Check if asset is currently allocated
	        Connection con = ConnectionHelper.getConnection();
	        String allocCheck = "SELECT employee_id, allocation_date FROM asset_allocations WHERE asset_id = ? AND return_date IS NULL";
	        PreparedStatement allocStmt = con.prepareStatement(allocCheck);
	        allocStmt.setInt(1, assetId);
	        ResultSet allocRs = allocStmt.executeQuery();

	        if (allocRs.next()) {
	            int empId = allocRs.getInt("employee_id");
	            Date allocDate = allocRs.getDate("allocation_date");

	            System.out.println("The asset is currently allocated to Employee ID: " + empId + " since " + allocDate);
	            System.out.print("Do you want to deallocate it first? (YES/NO): ");
	            String answer = sc.nextLine().trim();

	            if (answer.equalsIgnoreCase("YES")) {
	                System.out.print("Enter Return Date (yyyy-MM-dd): ");
	                String returnDate = sc.nextLine();
	                boolean deallocated = service.deallocateAsset(assetId, empId, returnDate);
	                if (deallocated) {
	                    System.out.println("Asset successfully deallocated. Proceeding with maintenance...");
	                } else {
	                    System.out.println("Deallocation failed. Cannot proceed with maintenance.");
	                    return;
	                }
	            } else {
	                System.out.println("Cannot perform maintenance while asset is in use.");
	                return;
	            }
	        }
	        
	        
	     // Step 2: Check if asset is currently reserved
	        String reserveCheck = "SELECT reservation_id, employee_id, start_date, end_date FROM reservations WHERE asset_id = ? AND status = 'Approved'";
	        PreparedStatement reserveStmt = con.prepareStatement(reserveCheck);
	        reserveStmt.setInt(1, assetId);
	        ResultSet reserveRs = reserveStmt.executeQuery();

	        if (reserveRs.next()) {
	            int resId = reserveRs.getInt("reservation_id");
	            int empId = reserveRs.getInt("employee_id");
	            Date start = reserveRs.getDate("start_date");
	            Date end = reserveRs.getDate("end_date");

	            System.out.println("The asset is currently reserved by Employee ID: " + empId + " from " + start + " to " + end);
	            System.out.print("Do you want to withdraw the reservation? (YES/NO): ");
	            String input = sc.nextLine().trim();

	            if (input.equalsIgnoreCase("YES")) {
	                boolean withdrawn = service.withdrawReservation(resId);
	                if (withdrawn) {
	                    System.out.println("Reservation withdrawn. Proceeding...");
	                } else {
	                    System.out.println("Failed to withdraw reservation. Cannot proceed.");
	                    return;
	                }
	            } else {
	                System.out.println("Cannot perform maintenance while asset is reserved.");
	                return;
	            }
	        }

	        // Proceed with maintenance
	        System.out.print("Enter Maintenance Date (yyyy-MM-dd): ");
	        String date = sc.nextLine();

	        System.out.print("Enter Description: ");
	        String desc = sc.nextLine();

	        System.out.print("Enter Cost: ");
	        double cost = sc.nextDouble();

	        boolean success = service.performMaintenance(assetId, date, desc, cost);
	        if (success) {
	            System.out.println("Maintenance recorded successfully.");
	        } else {
	            System.out.println("Failed to record maintenance.");
	        }

	    } catch (AssetNotFound e) {
	        System.out.println("Asset Not Found: " + e.getMessage());
	    } catch (Exception e) {
	        System.out.println("Error: " + e.getMessage());
	    }
	}

	

	public static void showAllMaintenanceRecordsMain() {
	    try {
	        List<MaintenanceRecord> records = service.showMaintenanceRecords();
	        if (records.isEmpty()) {
	            System.out.println("No maintenance records found.");
	        } else {
	            for (MaintenanceRecord rec : records) {
	                System.out.println("Maintenance ID: " + rec.getMaintenanceId());
	                System.out.println("Asset ID: " + rec.getAssetId());
	                System.out.println("Maintenance Date: " + rec.getMaintenanceDate());
	                System.out.println("Description: " + rec.getDescription());
	                System.out.println("Cost: " + rec.getCost());
	                System.out.println("-----------------------------------");
	            }
	        }
	    } catch (Exception e) {
	        System.out.println("Error fetching maintenance records: " + e.getMessage());
	    }
	}
	
	
	public static void reserveAssetMain() throws AssetNotFound, AssetNotMaintain {
	    try {
	        System.out.print("Enter Asset ID: ");
	        int assetId = sc.nextInt();
	        sc.nextLine(); // consume newline

	        // Check if asset exists (will throw AssetNotFound if not)
	        service.searchAsset(assetId);

	        // Collect other reservation details
	        System.out.print("Enter Employee ID: ");
	        int employeeId = sc.nextInt();
	        service.validateEmployeeExists(employeeId);
	        sc.nextLine(); // consume newline

	        System.out.print("Enter Reservation Date (yyyy-MM-dd): ");
	        String reservationDate = sc.nextLine();

	        System.out.print("Enter Start Date (yyyy-MM-dd): ");
	        String startDate = sc.nextLine();

	        System.out.print("Enter End Date (yyyy-MM-dd): ");
	        String endDate = sc.nextLine();

	        // Attempt reservation — messages will be shown inside the DAO method
	        service.reserveAsset(assetId, employeeId, reservationDate, startDate, endDate);

	    } catch (EmployeeNotFound e) {
	        System.out.println("Error: " + e.getMessage());
	        return;
	    }catch (ClassNotFoundException | SQLException e) {
	        System.out.println("Database error during reservation: " + e.getMessage());
	    }
	}

	
	public static void withdrawReservationMain() {
	    try {
	        System.out.print("Enter Reservation ID to withdraw: ");
	        int reservationId = sc.nextInt();

	        boolean result = service.withdrawReservation(reservationId);
	        if (result) {
	            System.out.println("Reservation withdrawn successfully.");
	        } else {
	            System.out.println("Failed to withdraw reservation.");
	        }
	    } catch (AssetNotFound e) {
	        System.out.println("Reservation not found: " + e.getMessage());
	    } catch (ClassNotFoundException | SQLException e) {
	        System.out.println("Error withdrawing reservation: " + e.getMessage());
	    }
	}

	public static void showAllReservationsMain() {
	    try {
	        List<Reservations> reservations = service.showAllReservations();
	        if (reservations.isEmpty()) {
	            System.out.println("No reservations found.");
	        } else {
	            for (Reservations res : reservations) {
	                System.out.println("Reservation ID: " + res.getReservationId());
	                System.out.println("Asset ID: " + res.getAssetId());
	                System.out.println("Employee ID: " + res.getEmployeeId());
	                System.out.println("Reservation Date: " + res.getReservationDate());
	                System.out.println("Start Date: " + res.getStartDate());
	                System.out.println("End Date: " + res.getEndDate());
	                System.out.println("Status: " + res.getStatus());
	                System.out.println("----------------------------------");
	            }
	        }
	    } catch (Exception e) {
	        System.out.println("Error retrieving reservations: " + e.getMessage());
	    }
	}


}
