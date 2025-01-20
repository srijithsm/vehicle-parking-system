package services;

import core.Vehicle;
import exceptions.ParkingFullException;
import exceptions.InvalidVehicleException;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public class ParkingManager {

    private static final String DB_URL = "jdbc:mysql://localhost:3306/?user=root";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "password";

    public ParkingManager() {
        // Initialize database connection if needed
    }

    // Park vehicle and store in database
    public void parkVehicle(Vehicle vehicle) throws InvalidVehicleException, ParkingFullException {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            // Check for available parking spot
            String findSpotQuery = "SELECT spot_id FROM ParkingSpots WHERE is_occupied = FALSE LIMIT 1";
            PreparedStatement findSpotStmt = conn.prepareStatement(findSpotQuery);
            ResultSet spotResult = findSpotStmt.executeQuery();

            if (spotResult.next()) {
                int spotId = spotResult.getInt("spot_id");

                // Insert vehicle information into Vehicles table
                String insertVehicleQuery = "INSERT INTO Vehicles (license_plate, vehicle_type, owner_name, owner_contact) VALUES (?, ?, ?, ?)";
                PreparedStatement insertVehicleStmt = conn.prepareStatement(insertVehicleQuery, Statement.RETURN_GENERATED_KEYS);
                insertVehicleStmt.setString(1, vehicle.getLicensePlate());
                insertVehicleStmt.setString(2, vehicle.getClass().getSimpleName());  // Vehicle type (Car, Bike)
                insertVehicleStmt.setString(3, vehicle.getOwnerName());
                insertVehicleStmt.setString(4, vehicle.getOwnerContact());
                insertVehicleStmt.executeUpdate();

                ResultSet generatedKeys = insertVehicleStmt.getGeneratedKeys();
                int vehicleId = -1;
                if (generatedKeys.next()) {
                    vehicleId = generatedKeys.getInt(1);
                }

                // Update ParkingSpots to set is_occupied = true
                String updateSpotQuery = "UPDATE ParkingSpots SET is_occupied = TRUE, vehicle_id = ? WHERE spot_id = ?";
                PreparedStatement updateSpotStmt = conn.prepareStatement(updateSpotQuery);
                updateSpotStmt.setInt(1, vehicleId);
                updateSpotStmt.setInt(2, spotId);
                updateSpotStmt.executeUpdate();

                // Insert parking record with entry time
                String insertRecordQuery = "INSERT INTO ParkingRecords (vehicle_id, spot_id, parked_time) VALUES (?, ?, ?)";
                PreparedStatement insertRecordStmt = conn.prepareStatement(insertRecordQuery);
                insertRecordStmt.setInt(1, vehicleId);
                insertRecordStmt.setInt(2, spotId);
                insertRecordStmt.setTimestamp(3, Timestamp.valueOf(LocalDateTime.now()));
                insertRecordStmt.executeUpdate();

                System.out.println("Vehicle parked successfully in spot ID: " + spotId);
            } else {
                throw new ParkingFullException("No available parking spots.");
            }
        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
        }
    }

    // Retrieve vehicle from database and calculate parking fee
    public double retrieveVehicle(String licensePlate) throws Exception {
        double fee = -1;

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            // Find vehicle and associated spot ID
            String vehicleQuery = "SELECT v.vehicle_id, v.owner_name, v.owner_contact, v.vehicle_type, p.spot_id, pr.parked_time " +
                                  "FROM Vehicles v " +
                                  "JOIN ParkingSpots p ON v.vehicle_id = p.vehicle_id " +
                                  "JOIN ParkingRecords pr ON v.vehicle_id = pr.vehicle_id " +
                                  "WHERE v.license_plate = ? AND p.is_occupied = TRUE";
            PreparedStatement vehicleStmt = conn.prepareStatement(vehicleQuery);
            vehicleStmt.setString(1, licensePlate);
            ResultSet rs = vehicleStmt.executeQuery();

            if (rs.next()) {
                int vehicleId = rs.getInt("vehicle_id");
                String ownerName = rs.getString("owner_name");
                String ownerContact = rs.getString("owner_contact");
                String vehicleType = rs.getString("vehicle_type");
                int spotId = rs.getInt("spot_id");
                LocalDateTime parkedTime = rs.getTimestamp("parked_time").toLocalDateTime();
                LocalDateTime exitTime = LocalDateTime.now();

                // Print the vehicle details
                System.out.println("Vehicle Details:");
                System.out.println("Owner Name: " + ownerName);
                System.out.println("Owner Contact: " + ownerContact);
                System.out.println("Vehicle Type: " + vehicleType);
                System.out.println("Parked Spot ID: " + spotId);

                // Calculate fee based on time parked
                long hours = ChronoUnit.HOURS.between(parkedTime, exitTime);
                String feeQuery = "SELECT hourly_rate FROM ParkingFees WHERE vehicle_type = ? AND spot_type = ?";
                PreparedStatement feeStmt = conn.prepareStatement(feeQuery);
                feeStmt.setString(1, vehicleType);
                feeStmt.setString(2, "Standard"); // Adjust according to your spot type logic
                ResultSet feeRs = feeStmt.executeQuery();
                if (feeRs.next()) {
                    fee = hours * feeRs.getDouble("hourly_rate");
                } else {
                    System.out.println("Fee rate not found for vehicle type.");
                }

                // Update ParkingRecords with exit time and calculated fee
                String updateRecordQuery = "UPDATE ParkingRecords SET exit_time = ?, parking_fee = ? WHERE vehicle_id = ?";
                PreparedStatement updateRecordStmt = conn.prepareStatement(updateRecordQuery);
                updateRecordStmt.setTimestamp(1, Timestamp.valueOf(exitTime));
                updateRecordStmt.setDouble(2, fee);
                updateRecordStmt.setInt(3, vehicleId);
                updateRecordStmt.executeUpdate();

                // Free up parking spot
                String updateSpotQuery = "UPDATE ParkingSpots SET is_occupied = FALSE, vehicle_id = NULL WHERE spot_id = ?";
                PreparedStatement updateSpotStmt = conn.prepareStatement(updateSpotQuery);
                updateSpotStmt.setInt(1, spotId);
                updateSpotStmt.executeUpdate();

                System.out.println("Vehicle retrieved. Total Parking Fee: $" + fee);
            } else {
                System.out.println("Vehicle not found or already retrieved.");
            }
        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
        }

        return fee;
    }

    // Calculate parking fee without retrieval
    public double calculateParkingFee(String licensePlate) {
        double fee = -1;

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            // Fetch parking record with both parked_time and exit_time
            String query = "SELECT pr.parked_time, pr.exit_time, pf.hourly_rate, " +
                           "TIMESTAMPDIFF(MINUTE, pr.parked_time, COALESCE(pr.exit_time, NOW())) AS minutes_parked " +
                           "FROM Vehicles v " +
                           "JOIN ParkingRecords pr ON v.vehicle_id = pr.vehicle_id " +
                           "JOIN ParkingFees pf ON v.vehicle_type = pf.vehicle_type " +
                           "WHERE v.license_plate = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, licensePlate);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                LocalDateTime parkedTime = rs.getTimestamp("parked_time").toLocalDateTime();
                LocalDateTime exitTime = rs.getTimestamp("exit_time") != null ? rs.getTimestamp("exit_time").toLocalDateTime() : null;
                long minutesParked = rs.getLong("minutes_parked"); // Minutes parked
                double hourlyRate = rs.getDouble("hourly_rate");

                // Debugging logs
                System.out.println("Parked Time: " + parkedTime);
                System.out.println("Exit Time: " + exitTime);
                System.out.println("Minutes Parked (Calculated): " + minutesParked);

                // Round up to the nearest hour if less than 1 hour (i.e., 60 minutes)
                long hoursParked = (minutesParked + 59) / 60; // This rounds up for any duration under 1 hour
                // Alternatively, use a check for the minimum fee threshold (e.g., 30 minutes)
                if (minutesParked < 30) {
                    hoursParked = 1;  // Consider this as 1 hour if parked less than 30 minutes
                }

                // Calculate fee
                fee = hoursParked * hourlyRate;
                System.out.println("Total parking fee for the vehicle: $" + fee);
            } else {
                System.out.println("Vehicle not found.");
            }
        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
        }

        return fee;
    }

}
