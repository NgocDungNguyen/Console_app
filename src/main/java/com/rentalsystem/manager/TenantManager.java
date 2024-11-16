package com.rentalsystem.manager;

import java.util.List;

import com.rentalsystem.model.Tenant;

public interface TenantManager {
    void addTenant(Tenant tenant);
    void updateTenant(Tenant tenant);
    void deleteTenant(String id);
    Tenant getTenant(String id);
    List<Tenant> getAllTenants();
    List<Tenant> searchTenants(String keyword);
    boolean isEmailTaken(String email);
    Tenant getTenantByEmail(String email);
    boolean updateTenant(Tenant tenant, String newEmail);
}