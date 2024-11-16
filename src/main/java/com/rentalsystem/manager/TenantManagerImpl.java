package com.rentalsystem.manager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.rentalsystem.model.Tenant;
import com.rentalsystem.model.RentalAgreement;
import com.rentalsystem.util.FileHandler;
import com.rentalsystem.util.InputValidator;

public class TenantManagerImpl implements TenantManager {
    private Map<String, Tenant> tenants;
    private FileHandler fileHandler;

    public TenantManagerImpl(FileHandler fileHandler) {
        this.fileHandler = fileHandler;
        this.tenants = new HashMap<>();
        loadTenants();
    }

    private void loadTenants() {
        List<Tenant> loadedTenants = fileHandler.loadTenants();
        for (Tenant tenant : loadedTenants) {
            tenants.put(tenant.getId(), tenant);
        }

        // Load rental agreements for each tenant
        List<RentalAgreement> agreements = fileHandler.loadRentalAgreements();
        for (RentalAgreement agreement : agreements) {
            Tenant tenant = tenants.get(agreement.getMainTenant().getId());
            if (tenant != null) {
                tenant.addRentalAgreement(agreement);
            }
        }
    }

    private void saveTenants() {
        List<String> lines = tenants.values().stream()
                .map(Tenant::toString)
                .collect(Collectors.toList());
        fileHandler.writeLines("tenants.txt", lines);
    }

    @Override
    public void addTenant(Tenant tenant) {
        if (!InputValidator.isValidEmail(tenant.getContactInformation())) {
            throw new IllegalArgumentException("Invalid email format.");
        }
        if (isEmailTaken(tenant.getContactInformation())) {
            throw new IllegalArgumentException("Email already in use: " + tenant.getContactInformation());
        }
        tenants.put(tenant.getId(), tenant);
        saveTenants();
    }


    @Override
    public void updateTenant(Tenant tenant) {
        if (!isValidEmail(tenant.getContactInformation())) {
            throw new IllegalArgumentException("Invalid email format for tenant: " + tenant.getContactInformation());
        }
        Tenant existingTenant = tenants.get(tenant.getId());
        if (existingTenant == null) {
            throw new IllegalArgumentException("Tenant with ID " + tenant.getId() + " does not exist.");
        }
        if (!existingTenant.getContactInformation().equals(tenant.getContactInformation()) && isEmailTaken(tenant.getContactInformation())) {
            throw new IllegalArgumentException("Email already in use: " + tenant.getContactInformation());
        }
        tenants.put(tenant.getId(), tenant);
        saveTenants();
    }

    @Override
    public void deleteTenant(String id) {
        if (tenants.remove(id) != null) {
            saveTenants();
        } else {
            throw new IllegalArgumentException("Tenant with ID " + id + " does not exist.");
        }
    }

    @Override
    public Tenant getTenant(String id) {
        return tenants.get(id);
    }

    @Override
    public List<Tenant> getAllTenants() {
        return new ArrayList<>(tenants.values());
    }

    @Override
    public List<Tenant> searchTenants(String keyword) {
        final String lowercaseKeyword = keyword.toLowerCase();
        return tenants.values().stream()
                .filter(tenant ->
                        tenant.getFullName().toLowerCase().contains(lowercaseKeyword) ||
                                tenant.getId().toLowerCase().contains(lowercaseKeyword) ||
                                tenant.getContactInformation().toLowerCase().contains(lowercaseKeyword))
                .collect(Collectors.toList());
    }

    @Override
    public Tenant getTenantByEmail(String email) {
        return tenants.values().stream()
                .filter(tenant -> tenant.getContactInformation().equalsIgnoreCase(email))
                .findFirst()
                .orElse(null);
    }

    @Override
    public boolean isEmailTaken(String email) {
        final String lowercaseEmail = email.toLowerCase();
        return tenants.values().stream()
                .anyMatch(tenant -> tenant.getContactInformation().toLowerCase().equals(lowercaseEmail));
    }

    @Override
    public boolean updateTenant(Tenant tenant, String newEmail) {
        if (!isValidEmail(newEmail)) {
            throw new IllegalArgumentException("Invalid email format for tenant: " + newEmail);
        }
        Tenant existingTenant = getTenantByEmail(newEmail);
        if (existingTenant != null && !existingTenant.getId().equals(tenant.getId())) {
            throw new IllegalArgumentException("Email is already in use by another tenant.");
        }
        tenant.setContactInformation(newEmail);
        tenants.put(tenant.getId(), tenant);
        saveTenants();
        return true;
    }

    public static boolean isValidEmail(String email) {
        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
        return email != null && email.matches(emailRegex);
    }
}
