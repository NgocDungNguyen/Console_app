package com.rentalsystem.model;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Tenant extends Person {
    private List<RentalAgreement> rentalAgreements;
    private List<Payment> paymentTransactions;
    private List<Property> rentedProperties;

    public Tenant(String id, String fullName, Date dateOfBirth, String contactInformation) {
        super(id, fullName, dateOfBirth, contactInformation);
        this.rentalAgreements = new ArrayList<>();
        this.paymentTransactions = new ArrayList<>();
        this.rentedProperties = new ArrayList<>();
    }

    public List<RentalAgreement> getRentalAgreements() {
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

    public List<Payment> getPaymentTransactions() {
        return new ArrayList<>(paymentTransactions);
    }

    public void addPaymentTransaction(Payment payment) {
        paymentTransactions.add(payment);
    }

    public List<Property> getRentedProperties() {
        return new ArrayList<>(rentedProperties);
    }

    public void addRentedProperty(Property property) {
        if (!rentedProperties.contains(property)) {
            rentedProperties.add(property);
        }
    }

    public void removeRentedProperty(Property property) {
        rentedProperties.remove(property);
    }

    @Override
    public void addManagedAgreement(RentalAgreement agreement) {
        addRentalAgreement(agreement);
    }

    @Override
    public String toString() {
        return String.join(",",
                getId(),
                getFullName(),
                new SimpleDateFormat("yyyy-MM-dd").format(getDateOfBirth()),
                getContactInformation()
        );
    }

    public static Tenant fromString(String line) {
        String[] parts = line.split(",");
        if (parts.length != 4) {
            throw new IllegalArgumentException("Invalid tenant data: " + line);
        }
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            return new Tenant(
                    parts[0],
                    parts[1],
                    dateFormat.parse(parts[2]),
                    parts[3]
            );
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid date format in tenant data: " + line, e);
        }
    }
}
