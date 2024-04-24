package in.hotel;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;

public class HotelReservation {

	private static final String url = "jdbc:mysql://localhost:3306/hotel_db";
	private static final String username = "root";
	private static final String password = "Rays@123";
	
	public static void main(String[] args) throws ClassNotFoundException, SQLException, InterruptedException {
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		
		try {
			Connection connection = DriverManager.getConnection(url, username, password);
			Scanner scanner = new Scanner(System.in);
			while(true) {
				System.out.println();
                System.out.println("Welcome to THE LEELA PALACE");
                System.out.println("1. Reserve a room");
                System.out.println("2. View Reservations");
                System.out.println("3. Get Room Number");
                System.out.println("4. Update Reservations");
                System.out.println("5. Delete Reservations");
                System.out.println("0. Exit");
                System.out.print("Choose an option: ");
                int choice = scanner.nextInt();
                switch(choice) {
                	case 1:{
                		reserveRoom(connection, scanner);
                		break;
                	}
                	case 2:{
                		viewReservation(connection);
                		break;
                	}
                	case 3:{
                		getRoomNumber(connection, scanner);
                		break;
                	}
                	case 4:{
                		updateReservation(connection, scanner);
                		break;
                	}
                	case 5:{
                		deleteReservation(connection, scanner);
                		break;
                	}
                	case 0:
                		exit();
                		scanner.close();
                		return;
                	default:
                		System.out.println("Invalid choice! Please enter correct choice");
                }
			}
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
	}
	

	private static void exit() throws InterruptedException{
		System.out.print("Exiting system");
		int i=5;
		while(i!=0) {
			System.out.print(".");
			Thread.sleep(1000);
			i--;
		}
		System.out.println();
		System.out.println("Thanks for staying at THE LEELA PALACE! Do visit us again soon");
		
	}

	private static void deleteReservation(Connection connection, Scanner scanner) {
		try {
			System.out.print("Enter reservation id to delete your reservation : ");
			int reservation_id = scanner.nextInt();
			
			if(!reservationExists(connection, reservation_id)) {
				System.out.println("Reservation not found for given ID!");
				return;
			}
			String delete_query = "delete from reservation where reservation_id="+reservation_id;
//			PreparedStatement preparedStatement = connection.prepareStatement(delete_query);
//			preparedStatement.setInt(1, reservation_id);
//			ResultSet resultSet = preparedStatement.executeQuery();
			
			try (Statement statement = connection.createStatement()) {
				int affectedRows = statement.executeUpdate(delete_query);
				
				if(affectedRows>0) {
					System.out.println("Reservation deleted successfully!");
				}
				else {
					System.out.println("Reservation cancellation failed!");
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
	}

	private static void updateReservation(Connection connection, Scanner scanner) {
		try {
			System.out.print("Enter reservion id to update : ");
			int reservation_id = scanner.nextInt();
			scanner.nextLine();
			
			if(!reservationExists(connection, reservation_id)) {
				System.out.println("Reservation not found for given ID");
				return;
			}
			System.out.print("Enter new guest name : ");
			String new_guest_name = scanner.next();
			System.out.print("Enter new room number : ");
			int new_room_number = scanner.nextInt();
			System.out.print("Enter new contact number : ");
			String new_contact_number = scanner.next();
			
			String update_query = "update reservation set guest_name=?, room_number=?, contact_number=? where reservation_id=?";
			PreparedStatement preparedStatement = connection.prepareStatement(update_query);
			preparedStatement.setString(1, new_guest_name);
			preparedStatement.setInt(2, new_room_number);
			preparedStatement.setString(3, new_contact_number);
			preparedStatement.setInt(4, reservation_id);
			
			int rows= preparedStatement.executeUpdate();
			if(rows>0) {
				System.out.println("Updated successfully!");
			}
			else {
				System.out.println("Update failed!");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private static boolean reservationExists(Connection connection, int reservation_id) {
		try {
			String reservation_query = "select reservation_id from reservation where reservation_id="+reservation_id;
			
			try (Statement statement = connection.createStatement()) {
				ResultSet resultSet = statement.executeQuery(reservation_query);
				return resultSet.next();
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	private static void getRoomNumber(Connection connection, Scanner scanner) {
		try {
			System.out.print("Enter reservation id : ");
			int reservation_id = scanner.nextInt();
			System.out.print("Enter guest name : ");
			String guest_name = scanner.next();
			
			String room_query = "select room_number from reservation where reservation_id=? and guest_name=?";
			PreparedStatement preparedStatement = connection.prepareStatement(room_query);
			preparedStatement.setInt(1, reservation_id);
			preparedStatement.setString(2, guest_name);
			try {
				ResultSet resultSet = preparedStatement.executeQuery();
				if(resultSet.next()) {
					int room_number = resultSet.getInt("room_number");
					System.out.println("Room for reservation id " +reservation_id+ " and guest "+guest_name+" is : "+room_number);
				}
				else {
					System.out.println("Reservation not found for the reservation_id and guest_name you entered");
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private static void viewReservation(Connection connection) {
		String view_query = "select * from reservation";
		
		try {
			PreparedStatement preparedStatement = connection.prepareStatement(view_query);
			ResultSet resultSet = preparedStatement.executeQuery();
			
			System.out.println("Current Reservations ");
			System.out.println("+----------------+-----------------+---------------+----------------------+-------------------------+");
            System.out.println("| Reservation ID | Guest           | Room Number   | Contact Number       | Reservation Date        |");
            System.out.println("+----------------+-----------------+---------------+----------------------+-------------------------+");
            
            while(resultSet.next()) {
            	int reservation_id = resultSet.getInt("reservation_id");
            	String guest_name = resultSet.getString("guest_name");
            	int room_number = resultSet.getInt("room_number");
            	String contact_number =  resultSet.getString("contact_number");
            	String reservationDate = resultSet.getString("reservation_date").toString();
            	
            	System.out.printf("|%-16d|%-17s|%-15d|%-22s|%-25s|",reservation_id, guest_name, room_number, contact_number, reservationDate);
            	System.out.println();
            }
            System.out.println("+----------------+-----------------+---------------+----------------------+-------------------------+");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void reserveRoom(Connection connection, Scanner scanner) {
		try {
			System.out.print("Enter the guest name : ");
			String guest_name = scanner.next();
			scanner.nextLine();
			System.out.print("Enter room number : ");
			int room_number = scanner.nextInt();
			System.out.print("Enter contact number : ");
			String contact_number = scanner.next();
			
			String reserve_query = "insert into reservation(guest_name, room_number, contact_number) values(?,?,?)";
			PreparedStatement preparedStatement = connection.prepareStatement(reserve_query);
			preparedStatement.setString(1, guest_name);
			preparedStatement.setInt(2, room_number);
			preparedStatement.setString(3, contact_number);
			
			int affectedRows = preparedStatement.executeUpdate();
			if(affectedRows>0) {
				System.out.println("Reservation Successfull!");
			}
			else {
				System.out.println("Reservation Failed!");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

}
