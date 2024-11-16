package com.rentalsystem.manager;

import com.rentalsystem.model.RentalAgreement;
import java.util.List;

public interface RentalManager {
    void addRentalAgreement(RentalAgreement agreement);
    void updateRentalAgreement(RentalAgreement agreement);
    void deleteRentalAgreement(String agreementId);
    RentalAgreement getRentalAgreement(String agreementId);
    List<RentalAgreement> getAllRentalAgreements();
    List<RentalAgreement> getSortedRentalAgreements(String sortBy);
    void saveToFile();
    void loadFromFile();
    void addSubTenant(String agreementId, String subTenantId);
    void removeSubTenant(String agreementId, String subTenantId);
    List<RentalAgreement> getActiveRentalAgreements();
    List<RentalAgreement> getExpiredRentalAgreements();
    double getTotalRentalIncome();
    int getTotalActiveAgreements();
    List<RentalAgreement> searchRentalAgreements(String keyword);
    void extendRentalAgreement(String agreementId, int extensionDays);
    void terminateRentalAgreement(String agreementId);
}