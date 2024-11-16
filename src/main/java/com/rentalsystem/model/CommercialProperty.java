package com.rentalsystem.model;

public class CommercialProperty extends Property {
    private String businessType;
    private int parkingSpaces;
    private double squareFootage;

    public CommercialProperty(String propertyId, String address, double price, PropertyStatus status, Owner owner,
                              String businessType, int parkingSpaces, double squareFootage) {
        super(propertyId, address, price, status, owner);
        this.businessType = businessType;
        this.parkingSpaces = parkingSpaces;
        this.squareFootage = squareFootage;
    }

    public String getBusinessType() {
        return businessType;
    }

    public void setBusinessType(String businessType) {
        this.businessType = businessType;
    }

    public int getParkingSpaces() {
        return parkingSpaces;
    }

    public void setParkingSpaces(int parkingSpaces) {
        this.parkingSpaces = parkingSpaces;
    }

    public double getSquareFootage() {
        return squareFootage;
    }

    public void setSquareFootage(double squareFootage) {
        this.squareFootage = squareFootage;
    }

    @Override
    public String toString() {
        return "CommercialProperty{" +
                "propertyId='" + getPropertyId() + '\'' +
                ", address='" + getAddress() + '\'' +
                ", price=" + getPrice() +
                ", status=" + getStatus() +
                ", owner=" + getOwner().getFullName() +
                ", businessType='" + businessType + '\'' +
                ", parkingSpaces=" + parkingSpaces +
                ", squareFootage=" + squareFootage +
                '}';
    }
}