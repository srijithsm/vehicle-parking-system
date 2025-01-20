package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;

public class DatabaseConnection {
    public static void main(String[] args) {
        String url = "jdbc:mysql://localhost:3306/vehicle_parking";
        String user = "root";
        String password = "srimathi@123";

        try (Connection conn = DriverManager.getConnection(url, user, password)) {
            // Example of inserting into Vehicles table
            String insertVehicles = "INSERT INTO Vehicles (vehicle_id, license_plate, vehicle_type, owner_name, owner_contact) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement pstmt = conn.prepareStatement(insertVehicles);
            
            pstmt.setInt(1, 5);
            pstmt.setString(2, "ST999JK");
            pstmt.setString(3, "Car");
            pstmt.setString(4, "Chris Evans");
            pstmt.setString(5, "555-7890");
            
            pstmt.executeUpdate();
            System.out.println("Inserted record into Vehicles table.");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}