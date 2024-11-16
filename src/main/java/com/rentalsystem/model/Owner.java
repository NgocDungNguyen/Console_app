package com.rentalsystem.model;

import java.util.*;

public class Owner extends Person {
    private List<Property> ownedProperties;
    private Set<Host> managingHosts;
    private List<RentalAgreement> rentalAgreements;

    public Owner(String id, String fullName, Date dateOfBirth, String contactInformation) {
        super(id, fullName, dateOfBirth, contactInformation);
        this.ownedProperties = new ArrayList<>();
        this.managingHosts = new HashSet<>();
        this.rentalAgreements = new ArrayList<>();
    }

    public List<Property> getOwnedProperties() {
        return new ArrayList<>(ownedProperties);
    }

    public void addOwnedProperty(Property property) {
        if (!ownedProperties.contains(property)) {
            ownedProperties.add(property);
        }
    }

    public void removeOwnedProperty(Property property) {
        ownedProperties.remove(property);
    }

    public Set<Host> getManagingHosts() {
        return new HashSet<>(managingHosts);
    }

    public void addManagingHost(Host host) {
        managingHosts.add(host);
    }

    public void removeManagingHost(Host host) {
        managingHosts.remove(host);
    }

    public List<RentalAgreement> getRentalAgreements() {
        return new ArrayList<>(rentalAgreements);
    }

    @Override
    public void addManagedAgreement(RentalAgreement agreement) {
        if (!rentalAgreements.contains(agreement)) {
            rentalAgreements.add(agreement);
        }
    }

    // You might want to add these methods as well
    public void removeManagedAgreement(RentalAgreement agreement) {
        rentalAgreements.remove(agreement);
    }

    public List<RentalAgreement> getManagedAgreements() {
        return new ArrayList<>(rentalAgreements);
    }

    public void addRentalAgreement(RentalAgreement agreement) {
        if (!rentalAgreements.contains(agreement)) {
            rentalAgreements.add(agreement);
        }
    }

    public void removeRentalAgreement(RentalAgreement agreement) {
        rentalAgreements.remove(agreement);
    }

    @Override
    public String toString() {
        return "Owner{" +
                "id='" + getId() + '\'' +
                ", fullName='" + getFullName() + '\'' +
                ", dateOfBirth=" + getDateOfBirth() +
                ", contactInformation='" + getContactInformation() + '\'' +
                ", ownedProperties=" + ownedProperties.size() +
                ", managingHosts=" + managingHosts.size() +
                ", rentalAgreements=" + rentalAgreements.size() +
                '}';
    }
}
