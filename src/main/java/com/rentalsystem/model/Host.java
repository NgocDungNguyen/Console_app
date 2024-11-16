package com.rentalsystem.model;

import java.util.*;

public class Host extends Person {
    private List<Property> managedProperties;
    private Set<Owner> cooperatingOwners;
    private List<RentalAgreement> managedAgreements;

    public Host(String id, String fullName, Date dateOfBirth, String contactInformation) {
        super(id, fullName, dateOfBirth, contactInformation);
        this.managedProperties = new ArrayList<>();
        this.cooperatingOwners = new HashSet<>();
        this.managedAgreements = new ArrayList<>();
    }

    public List<Property> getManagedProperties() {
        System.out.println("Host " + getId() + " has " + managedProperties.size() + " managed properties");
        return new ArrayList<>(managedProperties);
    }

    public void addManagedProperty(Property property) {
        if (!managedProperties.contains(property)) {
            managedProperties.add(property);
        }
    }

    public void removeManagedProperty(Property property) {
        if (managedProperties.remove(property)) {
            property.setHost(null);
        }
    }

    public void addManagedAgreement(RentalAgreement agreement) {
        if (!managedAgreements.contains(agreement)) {
            managedAgreements.add(agreement);
        }
    }

    public void removeManagedAgreement(RentalAgreement agreement) {
        managedAgreements.remove(agreement);
    }

    public List<RentalAgreement> getManagedAgreements() {
        return new ArrayList<>(managedAgreements);
    }

    public Set<Owner> getCooperatingOwners() {
        return new HashSet<>(cooperatingOwners);
    }

    public void addCooperatingOwner(Owner owner) {
        cooperatingOwners.add(owner);
        owner.addManagingHost(this);
    }

    public void removeCooperatingOwner(Owner owner) {
        if (cooperatingOwners.remove(owner)) {
            owner.removeManagingHost(this);
        }
    }

    @Override
    public String toString() {
        return "Host{" +
                "id='" + getId() + '\'' +
                ", fullName='" + getFullName() + '\'' +
                ", dateOfBirth=" + getDateOfBirth() +
                ", contactInformation='" + getContactInformation() + '\'' +
                ", managedProperties=" + managedProperties.size() +
                ", cooperatingOwners=" + cooperatingOwners.size() +
                ", managedAgreements=" + managedAgreements.size() +
                '}';
    }
}
