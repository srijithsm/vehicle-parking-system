package app;

import services.ParkingManager;
import core.Vehicle;
import core.Car;
import core.Bike;
import exceptions.InvalidVehicleException;

import java.util.Scanner;

public class ParkingApp {
    public static void main(String[] args) {
        ParkingManager manager = new ParkingManager();
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("\n--- Vehicle Parking System ---");
            System.out.println("1. Park Vehicle");
            System.out.println("2. Retrieve Vehicle");
            System.out.println("3. Calculate Fees");
            System.out.println("4. Exit");
            System.out.print("Enter your choice: ");
            
            int choice;
            try {
                choice = scanner.nextInt();
                scanner.nextLine(); // Consume newline character
            } catch (Exception e) {
                System.out.println("Invalid input. Please enter a number between 1 and 4.");
                scanner.nextLine(); // Clear invalid input
                continue;
            }

            switch (choice) {
                case 1:
                    // Park Vehicle
                    try {
                        System.out.print("Enter Vehicle Type (Car/Bike): ");
                        String type = scanner.nextLine().trim();
                        
                        System.out.print("Enter License Plate: ");
                        String licensePlate = scanner.nextLine().trim();
                        
                        System.out.print("Enter Owner's Name: ");
                        String ownerName = scanner.nextLine().trim();
                        
                        System.out.print("Enter Owner's Contact: ");
                        String ownerContact = scanner.nextLine().trim();
                        
                        Vehicle vehicle;
                        if (type.equalsIgnoreCase("Car")) {
                            vehicle = new Car(licensePlate, ownerName, ownerContact);
                        } else if (type.equalsIgnoreCase("Bike")) {
                            vehicle = new Bike(licensePlate, ownerName, ownerContact);
                        } else {
                            System.out.println("Invalid vehicle type.");
                            continue;
                        }

                        manager.parkVehicle(vehicle);
                    } catch (Exception e) {
                        System.out.println("Error parking vehicle: " + e.getMessage());
                    }
                    break;
                
                case 2:
                    // Retrieve Vehicle
                    System.out.print("Enter Vehicle License Plate to Retrieve: ");
                    String licensePlateToRetrieve = scanner.nextLine().trim();
                    try {
                        manager.retrieveVehicle(licensePlateToRetrieve);
                    } catch (Exception e) {
                        System.out.println("Error retrieving vehicle: " + e.getMessage());
                    }
                    break;
                
                case 3:
                    // Calculate Fees
                    System.out.print("Enter Vehicle License Plate to Calculate Fee: ");
                    String licensePlateForFee = scanner.nextLine().trim();
                    double fee = manager.calculateParkingFee(licensePlateForFee);
                    if (fee >= 0) {
                        System.out.println("Total parking fee: $" + fee);
                    } else {
                        System.out.println("Unable to calculate fee.");
                    }
                    break;

                case 4:
                    System.out.println("Exiting...");
                    scanner.close();
                    return;

                default:
                    System.out.println("Invalid choice, please select a number between 1 and 4.");
            }
        }
    }
}
