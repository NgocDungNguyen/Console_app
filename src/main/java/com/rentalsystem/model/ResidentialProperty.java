package com.rentalsystem.model;

public class ResidentialProperty extends Property {
    private int numberOfBedrooms;
    private boolean hasGarden;
    private boolean isPetFriendly;

    public ResidentialProperty(String propertyId, String address, double price, PropertyStatus status, Owner owner,
                               int numberOfBedrooms, boolean hasGarden, boolean isPetFriendly) {
        super(propertyId, address, price, status, owner);
        this.numberOfBedrooms = numberOfBedrooms;
        this.hasGarden = hasGarden;
        this.isPetFriendly = isPetFriendly;
    }

    public int getNumberOfBedrooms() {
        return numberOfBedrooms;
    }

    public void setNumberOfBedrooms(int numberOfBedrooms) {
        this.numberOfBedrooms = numberOfBedrooms;
    }

    public boolean hasGarden() {
        return hasGarden;
    }

    public void setHasGarden(boolean hasGarden) {
        this.hasGarden = hasGarden;
    }

    public boolean isPetFriendly() {
        return isPetFriendly;
    }

    public void setPetFriendly(boolean petFriendly) {
        isPetFriendly = petFriendly;
    }

    @Override
    public String toString() {
        return "ResidentialProperty{" +
                "propertyId='" + getPropertyId() + '\'' +
                ", address='" + getAddress() + '\'' +
                ", price=" + getPrice() +
                ", status=" + getStatus() +
                ", owner=" + getOwner().getFullName() +
                ", numberOfBedrooms=" + numberOfBedrooms +
                ", hasGarden=" + hasGarden +
                ", isPetFriendly=" + isPetFriendly +
                '}';
    }
}