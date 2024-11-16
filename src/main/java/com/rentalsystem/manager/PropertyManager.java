package com.rentalsystem.manager;

import com.rentalsystem.model.Property;

import java.util.List;

public interface PropertyManager {
    void addProperty(Property property);
    void updateProperty(Property property);
    void deleteProperty(String propertyId);
    Property getProperty(String propertyId);
    List<Property> getAvailableProperties();
    List<Property> getAllProperties();
    List<Property> searchProperties(String keyword);
    int getTotalProperties();
    int getOccupiedProperties();
}