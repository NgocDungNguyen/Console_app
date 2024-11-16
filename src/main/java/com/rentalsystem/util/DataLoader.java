package com.rentalsystem.util;

import com.rentalsystem.model.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DataLoader {
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
    private static final String DATA_DIRECTORY = "resources/data/";

    public static List<Tenant> loadTenants() throws IOException, ParseException {
        List<Tenant> tenants = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(DATA_DIRECTORY + "tenants.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                String id = parts[0].trim();
                String fullName = parts[1].trim();
                Date dateOfBirth = DATE_FORMAT.parse(parts[2].trim());
                String contactInfo = parts[3].trim();
                tenants.add(new Tenant(id, fullName, dateOfBirth, contactInfo));
            }
        }
        return tenants;
    }

    public static List<Owner> loadOwners() throws IOException, ParseException {
        List<Owner> owners = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(DATA_DIRECTORY + "owners.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                String id = parts[0].trim();
                String fullName = parts[1].trim();
                Date dateOfBirth = DATE_FORMAT.parse(parts[2].trim());
                String contactInfo = parts[3].trim();
                owners.add(new Owner(id, fullName, dateOfBirth, contactInfo));
            }
        }
        return owners;
    }

    public static List<Host> loadHosts() throws IOException, ParseException {
        List<Host> hosts = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(DATA_DIRECTORY + "hosts.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                String id = parts[0].trim();
                String fullName = parts[1].trim();
                Date dateOfBirth = DATE_FORMAT.parse(parts[2].trim());
                String contactInfo = parts[3].trim();
                hosts.add(new Host(id, fullName, dateOfBirth, contactInfo));
            }
        }
        return hosts;
    }

    public static List<Property> loadProperties() throws IOException {
        List<Property> properties = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(DATA_DIRECTORY + "properties.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                String type = parts[0].trim();
                String id = parts[1].trim();
                String address = parts[2].trim();
                double price = Double.parseDouble(parts[3].trim());
                Property.PropertyStatus status = Property.PropertyStatus.valueOf(parts[4].trim());
                String ownerId = parts[5].trim();
                
                if ("RESIDENTIAL".equals(type)) {
                    int bedrooms = Integer.parseInt(parts[6].trim());
                    boolean hasGarden = Boolean.parseBoolean(parts[7].trim());
                    boolean isPetFriendly = Boolean.parseBoolean(parts[8].trim());
                    properties.add(new ResidentialProperty(id, address, price, status, null, bedrooms, hasGarden, isPetFriendly));
                } else if ("COMMERCIAL".equals(type)) {
                    String businessType = parts[6].trim();
                    int parkingSpaces = Integer.parseInt(parts[7].trim());
                    double squareFootage = Double.parseDouble(parts[8].trim());
                    properties.add(new CommercialProperty(id, address, price, status, null, businessType, parkingSpaces, squareFootage));
                }
            }
        }
        return properties;
    }

    public static List<RentalAgreement> loadRentalAgreements() throws IOException, ParseException {
        List<RentalAgreement> agreements = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(DATA_DIRECTORY + "rental_agreements.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                String id = parts[0].trim();
                String propertyId = parts[1].trim();
                String tenantId = parts[2].trim();
                String ownerId = parts[3].trim();
                String hostId = parts[4].trim();
                Date startDate = DATE_FORMAT.parse(parts[5].trim());
                Date endDate = DATE_FORMAT.parse(parts[6].trim());
                double rentAmount = Double.parseDouble(parts[7].trim());
                RentalAgreement.RentalPeriod period = RentalAgreement.RentalPeriod.valueOf(parts[8].trim());
                RentalAgreement.Status status = RentalAgreement.Status.valueOf(parts[9].trim());

                RentalAgreement agreement = new RentalAgreement(id, null, null, null, null, startDate, endDate, rentAmount, period);
                agreement.setStatus(status);
                agreements.add(agreement);
            }
        }
        return agreements;
    }
}