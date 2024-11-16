package com.rentalsystem.manager;

import com.rentalsystem.model.Owner;
import java.util.List;

public interface OwnerManager {
    void addOwner(Owner owner);
    void updateOwner(Owner owner);
    void deleteOwner(String ownerId);
    Owner getOwner(String ownerId);
    List<Owner> getAllOwners();
    List<Owner> searchOwners(String keyword);
}