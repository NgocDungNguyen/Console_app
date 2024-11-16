package com.rentalsystem.manager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.rentalsystem.model.Owner;
import com.rentalsystem.util.FileHandler;
import com.rentalsystem.util.InputValidator;

public class OwnerManagerImpl implements OwnerManager {
    private Map<String, Owner> owners;
    private FileHandler fileHandler;

    public OwnerManagerImpl(FileHandler fileHandler) {
        this.fileHandler = fileHandler;
        this.owners = new HashMap<>();
        loadOwners();
    }

    private void loadOwners() {
        List<Owner> loadedOwners = fileHandler.loadOwners();
        for (Owner owner : loadedOwners) {
            owners.put(owner.getId(), owner);
        }
    }

    @Override
    public void addOwner(Owner owner) {
        if (!InputValidator.isValidEmail(owner.getContactInformation())) {
            throw new IllegalArgumentException("Invalid email format for owner: " + owner.getContactInformation());
        }
        if (isEmailTaken(owner.getContactInformation())) {
            throw new IllegalArgumentException("Email already in use: " + owner.getContactInformation());
        }
        owners.put(owner.getId(), owner);
        saveOwners();
    }

    @Override
    public void updateOwner(Owner owner) {
        if (!InputValidator.isValidEmail(owner.getContactInformation())) {
            throw new IllegalArgumentException("Invalid email format for owner: " + owner.getContactInformation());
        }
        Owner existingOwner = owners.get(owner.getId());
        if (existingOwner == null) {
            throw new IllegalArgumentException("Owner with ID " + owner.getId() + " does not exist.");
        }
        if (!existingOwner.getContactInformation().equals(owner.getContactInformation()) && isEmailTaken(owner.getContactInformation())) {
            throw new IllegalArgumentException("Email already in use: " + owner.getContactInformation());
        }
        owners.put(owner.getId(), owner);
        saveOwners();
    }

    private boolean isEmailTaken(String email) {
        return owners.values().stream()
                .anyMatch(owner -> owner.getContactInformation().equalsIgnoreCase(email));
    }

    @Override
    public void deleteOwner(String ownerId) {
        if (!owners.containsKey(ownerId)) {
            throw new IllegalArgumentException("Owner with ID " + ownerId + " does not exist.");
        }
        owners.remove(ownerId);
        saveOwners();
    }

    @Override
    public Owner getOwner(String ownerId) {
        Owner owner = owners.get(ownerId);
        if (owner == null) {
            throw new IllegalArgumentException("Owner with ID " + ownerId + " does not exist.");
        }
        return owner;
    }

    @Override
    public List<Owner> getAllOwners() {
        return new ArrayList<>(owners.values());
    }

    @Override
    public List<Owner> searchOwners(String keyword) {
        final String lowercaseKeyword = keyword.toLowerCase();
        return owners.values().stream()
                .filter(owner -> owner.getFullName().toLowerCase().contains(lowercaseKeyword) ||
                        owner.getId().toLowerCase().contains(lowercaseKeyword) ||
                        owner.getContactInformation().toLowerCase().contains(lowercaseKeyword))
                .collect(Collectors.toList());
    }

    private void saveOwners() {
        fileHandler.saveOwners(new ArrayList<>(owners.values()));
    }
}