package core;

public class ParkingSpot {
    private int spotId;
    private String spotType;
    private boolean isOccupied;
    private int vehicleId; // References Vehicles table

    public ParkingSpot(int spotId, String spotType) {
        this.spotId = spotId;
        this.spotType = spotType;
        this.isOccupied = false;
    }

    public int getSpotId() { return spotId; }
    public String getSpotType() { return spotType; }
    public boolean isOccupied() { return isOccupied; }
    public int getVehicleId() { return vehicleId; }

    public void occupySpot(int vehicleId) {
        this.isOccupied = true;
        this.vehicleId = vehicleId;
    }

    public void freeSpot() {
        this.isOccupied = false;
        this.vehicleId = 0;
    }
}