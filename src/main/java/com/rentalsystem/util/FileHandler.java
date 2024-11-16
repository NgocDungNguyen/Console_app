package com.rentalsystem.util;

import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

import com.rentalsystem.model.*;

public class FileHandler {
    private static final String DATA_DIRECTORY = "resources/data/";
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

    public List<String> readLines(String filename) {
        List<String> lines = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(DATA_DIRECTORY + filename))) {
            String line;
            while ((line = reader.readLine()) != null) {
                lines.add(line);
            }
        } catch (IOException e) {
            System.err.println("Error reading file: " + filename);
            e.printStackTrace();
        }
        return lines;
    }

    public void writeLines(String filename, List<String> lines) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(DATA_DIRECTORY + filename))) {
            for (String line : lines) {
                writer.write(line);
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("Error writing to file: " + filename);
            e.printStackTrace();
        }
    }

    public void saveRentalAgreements(List<RentalAgreement> agreements) {
        List<String> lines = new ArrayList<>();
        for (RentalAgreement agreement : agreements) {
            lines.add(String.join(",",
                    agreement.getAgreementId(),
                    agreement.getProperty().getPropertyId(),
                    agreement.getMainTenant().getId(),
                    agreement.getOwner().getId(),
                    agreement.getHost().getId(),
                    DATE_FORMAT.format(agreement.getStartDate()),
                    DATE_FORMAT.format(agreement.getEndDate()),
                    String.valueOf(agreement.getRentAmount()),
                    agreement.getRentalPeriod().toString(),
                    agreement.getStatus().toString()
            ));

            for (Tenant subTenant : agreement.getSubTenants()) {
                lines.add("SUB," + agreement.getAgreementId() + "," + subTenant.getId());
            }
        }
        writeLines("rental_agreements.txt", lines);
    }

    public List<RentalAgreement> loadRentalAgreements() {
        List<RentalAgreement> agreements = new ArrayList<>();
        Map<String, RentalAgreement> agreementMap = new HashMap<>();

        List<String> lines = readLines("rental_agreements.txt");
        for (String line : lines) {
            String[] parts = line.split(",");
            if (parts[0].equals("SUB")) {
                RentalAgreement agreement = agreementMap.get(parts[1]);
                Tenant subTenant = getTenantById(parts[2]);
                if (agreement != null && subTenant != null) {
                    agreement.addSubTenant(subTenant);
                }
            } else if (parts.length == 10) {
                try {
                    Property property = getPropertyById(parts[1]);
                    Tenant mainTenant = getTenantById(parts[2]);
                    Owner owner = getOwnerById(parts[3]);
                    Host host = getHostById(parts[4]);

                    if (property != null && mainTenant != null && owner != null && host != null) {
                        RentalAgreement agreement = new RentalAgreement(
                                parts[0],
                                property,
                                mainTenant,
                                owner,
                                host,
                                DATE_FORMAT.parse(parts[5]),
                                DATE_FORMAT.parse(parts[6]),
                                Double.parseDouble(parts[7]),
                                RentalAgreement.RentalPeriod.valueOf(parts[8])
                        );
                        agreement.setStatus(RentalAgreement.Status.valueOf(parts[9]));
                        agreements.add(agreement);
                        agreementMap.put(agreement.getAgreementId(), agreement);
                    }
                } catch (ParseException | IllegalArgumentException e) {
                    System.err.println("Error parsing rental agreement: " + String.join(",", parts));
                    e.printStackTrace();
                }
            }
        }
        return agreements;
    }

    public void saveProperties(List<Property> properties) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(DATA_DIRECTORY + "properties.txt"))) {
            for (Property property : properties) {
                List<String> propertyData = new ArrayList<>(Arrays.asList(
                        property instanceof ResidentialProperty ? "RESIDENTIAL" : "COMMERCIAL",
                        property.getPropertyId(),
                        property.getAddress(),
                        String.valueOf(property.getPrice()),
                        property.getStatus().toString(),
                        property.getOwner().getId(),
                        property.getHost() != null ? property.getHost().getId() : "",
                        property.getCurrentTenant() != null ? property.getCurrentTenant().getId() : ""
                ));

                if (property instanceof ResidentialProperty) {
                    ResidentialProperty rp = (ResidentialProperty) property;
                    propertyData.addAll(Arrays.asList(
                            String.valueOf(rp.getNumberOfBedrooms()),
                            String.valueOf(rp.hasGarden()),
                            String.valueOf(rp.isPetFriendly())
                    ));
                } else if (property instanceof CommercialProperty) {
                    CommercialProperty cp = (CommercialProperty) property;
                    propertyData.addAll(Arrays.asList(
                            cp.getBusinessType(),
                            String.valueOf(cp.getParkingSpaces()),
                            String.valueOf(cp.getSquareFootage())
                    ));
                }

                writer.println(String.join(",", propertyData));
            }
        } catch (IOException e) {
            System.err.println("Error saving properties");
            e.printStackTrace();
        }
    }

    public List<Property> loadProperties() {
        List<Property> properties = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(DATA_DIRECTORY + "properties.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length < 6) {
                    continue;
                }

                Owner owner = getOwnerById(parts[5]);
                if (owner == null) {
                    continue;
                }

                Property property;
                if (parts[0].equals("RESIDENTIAL")) {
                    property = new ResidentialProperty(
                            parts[1], parts[2], Double.parseDouble(parts[3]),
                            Property.PropertyStatus.valueOf(parts[4]), owner,
                            Integer.parseInt(parts[7]), Boolean.parseBoolean(parts[8]),
                            Boolean.parseBoolean(parts[9])
                    );
                } else if (parts[0].equals("COMMERCIAL")) {
                    property = new CommercialProperty(
                            parts[1], parts[2], Double.parseDouble(parts[3]),
                            Property.PropertyStatus.valueOf(parts[4]), owner,
                            parts[7], Integer.parseInt(parts[8]), Double.parseDouble(parts[9])
                    );
                } else {
                    continue;
                }

                if (parts.length > 6 && !parts[6].isEmpty()) {
                    Host host = getHostById(parts[6]);
                    if (host != null) {
                        property.setHost(host);
                    }
                }

                properties.add(property);
            }
        } catch (IOException | NumberFormatException e) {
            System.err.println("Error loading properties: " + e.getMessage());
            e.printStackTrace();
        }
        return properties;
    }

    public void saveTenants(List<Tenant> tenants) {
        List<String> lines = tenants.stream().map(Tenant::toString).collect(Collectors.toList());
        writeLines("tenants.txt", lines);
    }

    public List<Tenant> loadTenants() {
        return readLines("tenants.txt").stream()
                .map(line -> {
                    try {
                        return Tenant.fromString(line);
                    } catch (IllegalArgumentException e) {
                        System.err.println("Error parsing tenant: " + line);
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    public void saveOwners(List<Owner> owners) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(DATA_DIRECTORY + "owners.txt"))) {
            for (Owner owner : owners) {
                writer.println(String.join(",",
                        owner.getId(),
                        owner.getFullName(),
                        DATE_FORMAT.format(owner.getDateOfBirth()),
                        owner.getContactInformation()
                ));
            }
        } catch (IOException e) {
            System.err.println("Error saving owners");
            e.printStackTrace();
        }
    }

    public List<Owner> loadOwners() {
        List<Owner> owners = new ArrayList<>();
        for (String line : readLines("owners.txt")) {
            String[] parts = line.split(",");
            if (parts.length == 4) {
                try {
                    owners.add(new Owner(
                            parts[0],
                            parts[1],
                            DATE_FORMAT.parse(parts[2]),
                            parts[3]
                    ));
                } catch (ParseException e) {
                    System.err.println("Error parsing owner: " + line);
                }
            }
        }
        return owners;
    }

    public void saveHosts(List<Host> hosts) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(DATA_DIRECTORY + "hosts.txt"))) {
            for (Host host : hosts) {
                writer.println(String.join(",",
                        host.getId(),
                        host.getFullName(),
                        DATE_FORMAT.format(host.getDateOfBirth()),
                        host.getContactInformation()
                ));
            }
        } catch (IOException e) {
            System.err.println("Error saving hosts");
            e.printStackTrace();
        }
    }

    public List<Host> loadHosts() {
        List<Host> hosts = new ArrayList<>();
        for (String line : readLines("hosts.txt")) {
            String[] parts = line.split(",");
            if (parts.length == 4) {
                try {
                    hosts.add(new Host(
                            parts[0],
                            parts[1],
                            DATE_FORMAT.parse(parts[2]),
                            parts[3]
                    ));
                } catch (ParseException e) {
                    System.err.println("Error parsing host: " + line);
                }
            }
        }
        return hosts;
    }

    private Property getPropertyById(String propertyId) {
        return loadProperties().stream()
                .filter(p -> p.getPropertyId().equals(propertyId))
                .findFirst()
                .orElse(null);
    }

    private Tenant getTenantById(String tenantId) {
        return loadTenants().stream()
                .filter(t -> t.getId().equals(tenantId))
                .findFirst()
                .orElse(null);
    }

    private Owner getOwnerById(String ownerId) {
        return loadOwners().stream()
                .filter(o -> o.getId().equals(ownerId))
                .findFirst()
                .orElse(null);
    }

    private Host getHostById(String hostId) {
        return loadHosts().stream()
                .filter(h -> h.getId().equals(hostId))
                .findFirst()
                .orElse(null);
    }
}
