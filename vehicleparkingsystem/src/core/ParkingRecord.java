package core;

import java.util.Date;
import java.util.concurrent.TimeUnit;

public class ParkingRecord {
    private int recordId;
    private int vehicleId;
    private int spotId;
    private Date parkedTime;
    private Date exitTime;
    private double parkingFee;

    // Constructor for parking record
    public ParkingRecord(int vehicleId, int spotId, Date parkedTime) {
        this.vehicleId = vehicleId;
        this.spotId = spotId;
        this.parkedTime = parkedTime;
        this.exitTime = null; // Initially, exit time is null
        this.parkingFee = 0.0; // Initialize fee to 0
    }

    // Setters and Getters
    public int getRecordId() { return recordId; }
    public void setRecordId(int recordId) { this.recordId = recordId; }

    public int getVehicleId() { return vehicleId; }
    public void setVehicleId(int vehicleId) { this.vehicleId = vehicleId; }

    public int getSpotId() { return spotId; }
    public void setSpotId(int spotId) { this.spotId = spotId; }

    public Date getParkedTime() { return parkedTime; }
    public void setParkedTime(Date parkedTime) { this.parkedTime = parkedTime; }

    public Date getExitTime() { return exitTime; }
    public void setExitTime(Date exitTime) { this.exitTime = exitTime; }

    public double getParkingFee() { return parkingFee; }
    public void setParkingFee(double parkingFee) { this.parkingFee = parkingFee; }

    // Method to calculate parking duration in hours
    public long getParkedDurationInMinutes() {
        if (exitTime == null) {
            return 0; // If exit time is not set, return 0 minutes
        }
        long durationInMillis = exitTime.getTime() - parkedTime.getTime();
        return TimeUnit.MILLISECONDS.toMinutes(durationInMillis); // Convert milliseconds to minutes
    }

    // Method to calculate the parking fee based on hourly rate
    public double calculateParkingFee(double hourlyRate) {
        long parkedDurationInMinutes = getParkedDurationInMinutes();
        if (parkedDurationInMinutes <= 0) {
            return 0.0; // If parked duration is less than or equal to 0, no fee is applied
        }

        // Round up the minutes to the nearest full hour for charging
        long hoursParked = (parkedDurationInMinutes + 59) / 60; // This ensures rounding up
        return hoursParked * hourlyRate;
    }

    // Optional: Method to print parking details for debugging
    public void printParkingDetails() {
        System.out.println("Vehicle ID: " + vehicleId);
        System.out.println("Spot ID: " + spotId);
        System.out.println("Parked Time: " + parkedTime);
        System.out.println("Exit Time: " + (exitTime != null ? exitTime : "Still Parked"));
        System.out.println("Total Duration: " + getParkedDurationInMinutes() + " minutes");
        System.out.println("Parking Fee: $" + parkingFee);
    }
}
