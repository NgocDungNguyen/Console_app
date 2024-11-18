package com.rentalsystem.manager;

import com.rentalsystem.model.*;
import com.rentalsystem.util.FileHandler;
import java.util.*;
import java.util.stream.Collectors;

public class PropertyManagerImpl implements PropertyManager {
    private Map<String, Property> properties;
    private FileHandler fileHandler;
    private HostManager hostManager;
    private TenantManager tenantManager;
    private OwnerManager ownerManager;

    public PropertyManagerImpl(FileHandler fileHandler, HostManager hostManager, TenantManager tenantManager, OwnerManager ownerManager) {
        this.fileHandler = fileHandler;
        this.hostManager = hostManager;
        this.tenantManager = tenantManager;
        this.ownerManager = ownerManager;
        this.properties = new HashMap<>();
        loadProperties();
    }

    private void loadProperties() {
        List<Property> loadedProperties = fileHandler.loadProperties();
        for (Property property : loadedProperties) {
            properties.put(property.getPropertyId(), property);

            if (property.getHost() != null) {
                Host host = hostManager.getHost(property.getHost().getId());
                if (host != null) {
                    property.setHost(host);
                }
            }

            if (property.getOwner() != null) {
                Owner owner = ownerManager.getOwner(property.getOwner().getId());
                if (owner != null) {
                    property.setOwner(owner);
                }
            }

            if (property.getStatus() == Property.PropertyStatus.RENTED) {
                RentalAgreement agreement = findActiveRentalAgreement(property);
                if (agreement != null) {
                    Tenant tenant = tenantManager.getTenant(agreement.getMainTenant().getId());
                    if (tenant != null) {
                        property.addTenant(tenant);
                    }
                }
            }
        }
    }

    private RentalAgreement findActiveRentalAgreement(Property property) {
        List<RentalAgreement> agreements = fileHandler.loadRentalAgreements();
        return agreements.stream()
                .filter(a -> a.getProperty().getPropertyId().equals(property.getPropertyId()) && a.getStatus() == RentalAgreement.Status.ACTIVE)
                .findFirst()
                .orElse(null);
    }

    @Override
    public void addProperty(Property property) {
        if (properties.containsKey(property.getPropertyId())) {
            throw new IllegalArgumentException("Property with ID " + property.getPropertyId() + " already exists.");
        }
        properties.put(property.getPropertyId(), property);
        saveProperties();
    }

    @Override
    public void updateProperty(Property property) {
        if (!property.getTenants().isEmpty()) {
            property.setStatus(Property.PropertyStatus.RENTED);
        } else if (property.getStatus() != Property.PropertyStatus.UNDER_MAINTENANCE) {
            property.setStatus(Property.PropertyStatus.AVAILABLE);
        }
        properties.put(property.getPropertyId(), property);
        saveProperties();
    }

    @Override
    public void deleteProperty(String propertyId) {
        Property property = properties.remove(propertyId);
        if (property == null) {
            throw new IllegalArgumentException("Property with ID " + propertyId + " does not exist.");
        }
        if (property.getHost() != null) {
            property.getHost().removeManagedProperty(property);
        }
        for (Tenant tenant : property.getTenants()) {
            tenant.removeRentedProperty(property);
        }
        if (property.getOwner() != null) {
            property.getOwner().removeOwnedProperty(property);
        }
        saveProperties();
    }

    @Override
    public Property getProperty(String propertyId) {
        Property property = properties.get(propertyId);
        if (property == null) {
            throw new IllegalArgumentException("Property with ID " + propertyId + " does not exist.");
        }
        return property;
    }

    @Override
    public List<Property> getAllProperties() {
        return new ArrayList<>(properties.values());
    }

    @Override
    public List<Property> getSorted(String sortBy) {
        List<Property> sortedList = new ArrayList<>(properties.values());
        switch (sortBy.toLowerCase()) {
            case "id":
                sortedList.sort(Comparator.comparing(Property::getPropertyId));
                break;
            case "type":
                sortedList.sort(Comparator.comparing(p -> p instanceof ResidentialProperty ? "Residential" : "Commercial"));
                break;
            case "address":
                sortedList.sort(Comparator.comparing(Property::getAddress));
                break;
            case "price":
                sortedList.sort(Comparator.comparing(Property::getPrice));
                break;
            case "status":
                sortedList.sort(Comparator.comparing(Property::getStatus));
                break;
            case "owner":
                sortedList.sort(Comparator.comparing(p -> p.getOwner().getFullName()));
                break;
            default:
                throw new IllegalArgumentException("Invalid sort criteria: " + sortBy);
        }
        return sortedList;
    }

    @Override
    public int getTotalProperties() {
        return properties.size();
    }

    @Override
    public int getOccupiedProperties() {
        return (int) properties.values().stream()
                .filter(p -> p.getStatus() == Property.PropertyStatus.RENTED)
                .count();
    }

    @Override
    public List<Property> searchProperties(String keyword) {
        final String lowercaseKeyword = keyword.toLowerCase();
        return properties.values().stream()
                .filter(property -> property.getPropertyId().toLowerCase().contains(lowercaseKeyword) ||
                        property.getAddress().toLowerCase().contains(lowercaseKeyword) ||
                        property.getOwner().getFullName().toLowerCase().contains(lowercaseKeyword))
                .collect(Collectors.toList());
    }

    @Override
    public List<Property> getAvailableProperties() {
        return properties.values().stream()
                .filter(property -> property.getStatus() == Property.PropertyStatus.AVAILABLE)
                .collect(Collectors.toList());
    }

    private void saveProperties() {
        fileHandler.saveProperties(new ArrayList<>(properties.values()));
    }
}
