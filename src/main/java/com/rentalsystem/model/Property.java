package com.rentalsystem.model;

import java.util.Objects;
import java.util.ArrayList;
import java.util.List;

public abstract class Property {
    private String propertyId;
    private String address;
    private double price;
    private PropertyStatus status;
    private Owner owner;
    private Host host;
    private Tenant currentTenant;
    private List<RentalAgreement> rentalHistory;

    public enum PropertyStatus {
        AVAILABLE, RENTED, UNDER_MAINTENANCE
    }

    public Property(String propertyId, String address, double price, PropertyStatus status, Owner owner) {
        this.propertyId = propertyId;
        this.address = address;
        this.price = price;
        this.status = status;
        this.owner = owner;
        this.rentalHistory = new ArrayList<>();
    }

    public String getPropertyId() {
        return propertyId;
    }

    public void setPropertyId(String propertyId) {
        this.propertyId = propertyId;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public PropertyStatus getStatus() {
        return status;
    }

    public void setStatus(PropertyStatus status) {
        this.status = status;
    }

    public Owner getOwner() {
        return owner;
    }

    public void setOwner(Owner owner) {
        this.owner = owner;
    }

    public Host getHost() {
        return host;
    }

    public void setHost(Host newHost) {
        if (this.host == newHost) {
            return;
        }

        Host oldHost = this.host;
        this.host = newHost;

        if (oldHost != null) {
            oldHost.removeManagedProperty(this);
        }

        if (newHost != null) {
            newHost.addManagedProperty(this);
        }
    }

    public Tenant getCurrentTenant() {
        return currentTenant;
    }

    public void setCurrentTenant(Tenant tenant) {
        if (this.currentTenant != null) {
            this.currentTenant.removeRentedProperty(this);
        }
        this.currentTenant = tenant;
        if (tenant != null) {
            tenant.addRentedProperty(this);
            this.status = PropertyStatus.RENTED;
        } else {
            this.status = PropertyStatus.AVAILABLE;
        }
    }

    public void addRentalAgreement(RentalAgreement agreement) {
        rentalHistory.add(agreement);
    }

    public List<RentalAgreement> getRentalHistory() {
        return new ArrayList<>(rentalHistory);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Property property = (Property) o;
        return Objects.equals(propertyId, property.propertyId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(propertyId);
    }

    @Override
    public String toString() {
        return "Property{" +
                "propertyId='" + propertyId + '\'' +
                ", address='" + address + '\'' +
                ", price=" + price +
                ", status=" + status +
                ", owner=" + (owner != null ? owner.getId() + " - " + owner.getFullName() : "N/A") +
                ", host=" + (host != null ? host.getId() + " - " + host.getFullName() : "N/A") +
                ", currentTenant=" + (currentTenant != null ? currentTenant.getId() + " - " + currentTenant.getFullName() : "N/A") +
                '}';
    }
}
