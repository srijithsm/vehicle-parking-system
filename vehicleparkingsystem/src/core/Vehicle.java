package core;

public abstract class Vehicle {
    private String licensePlate;
    private String ownerName;
    private String ownerContact;

    public Vehicle(String licensePlate, String ownerName, String ownerContact) {
        this.licensePlate = licensePlate;
        this.ownerName = ownerName;
        this.ownerContact = ownerContact;
    }

    // Getters
    public String getLicensePlate() {
        return licensePlate;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public String getOwnerContact() {
        return ownerContact;
    }
    
    // Other common methods for Vehicle, if any
}