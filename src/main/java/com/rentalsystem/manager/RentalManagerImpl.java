package com.rentalsystem.manager;

import com.rentalsystem.model.RentalAgreement;
import com.rentalsystem.model.Tenant;
import com.rentalsystem.util.FileHandler;
import java.util.*;
import java.util.stream.Collectors;

public class RentalManagerImpl implements RentalManager {
    private Map<String, RentalAgreement> rentalAgreements;
    private FileHandler fileHandler;
    private TenantManager tenantManager;
    private PropertyManager propertyManager;
    private HostManager hostManager;
    private OwnerManager ownerManager;

    public RentalManagerImpl(FileHandler fileHandler, TenantManager tenantManager, PropertyManager propertyManager, HostManager hostManager, OwnerManager ownerManager) {        this.fileHandler = fileHandler;
        this.tenantManager = tenantManager;
        this.propertyManager = propertyManager;
        this.hostManager = hostManager;
        this.ownerManager = ownerManager;
        this.rentalAgreements = new HashMap<>();
        loadRentalAgreements();
    }

    private void loadRentalAgreements() {
        List<RentalAgreement> loadedAgreements = fileHandler.loadRentalAgreements();
        for (RentalAgreement agreement : loadedAgreements) {
            rentalAgreements.put(agreement.getAgreementId(), agreement);

            // Associate the agreement with the tenant
            Tenant tenant = tenantManager.getTenant(agreement.getMainTenant().getId());
            if (tenant != null) {
                tenant.addRentalAgreement(agreement);
            }
        }
    }

    @Override
    public void addRentalAgreement(RentalAgreement agreement) {
        if (rentalAgreements.containsKey(agreement.getAgreementId())) {
            throw new IllegalArgumentException("Rental agreement with ID " + agreement.getAgreementId() + " already exists.");
        }
        rentalAgreements.put(agreement.getAgreementId(), agreement);

        // Update related entities
        agreement.getProperty().setCurrentTenant(agreement.getMainTenant());
        agreement.getProperty().addRentalAgreement(agreement);
        agreement.getMainTenant().addRentalAgreement(agreement);
        agreement.getHost().addManagedAgreement(agreement);
        agreement.getOwner().addRentalAgreement(agreement);

        saveRentalAgreements();
    }

    @Override
    public void updateRentalAgreement(RentalAgreement agreement) {
        if (!rentalAgreements.containsKey(agreement.getAgreementId())) {
            throw new IllegalArgumentException("Rental agreement with ID " + agreement.getAgreementId() + " does not exist.");
        }
        rentalAgreements.put(agreement.getAgreementId(), agreement);
        saveRentalAgreements();
    }

    @Override
    public void deleteRentalAgreement(String agreementId) {
        if (!rentalAgreements.containsKey(agreementId)) {
            throw new IllegalArgumentException("Rental agreement with ID " + agreementId + " does not exist.");
        }
        rentalAgreements.remove(agreementId);
        saveRentalAgreements();
    }

    @Override
    public RentalAgreement getRentalAgreement(String agreementId) {
        RentalAgreement agreement = rentalAgreements.get(agreementId);
        if (agreement == null) {
            throw new IllegalArgumentException("Rental agreement with ID " + agreementId + " does not exist.");
        }
        return agreement;
    }

    @Override
    public List<RentalAgreement> getAllRentalAgreements() {
        return new ArrayList<>(rentalAgreements.values());
    }

    @Override
    public List<RentalAgreement> getSortedRentalAgreements(String sortBy) {
        List<RentalAgreement> sortedList = new ArrayList<>(rentalAgreements.values());
        switch (sortBy.toLowerCase()) {
            case "id":
                sortedList.sort(Comparator.comparing(RentalAgreement::getAgreementId));
                break;
            case "startdate":
                sortedList.sort(Comparator.comparing(RentalAgreement::getStartDate));
                break;
            case "enddate":
                sortedList.sort(Comparator.comparing(RentalAgreement::getEndDate));
                break;
            case "rent":
                sortedList.sort(Comparator.comparing(RentalAgreement::getRentAmount));
                break;
            default:
                throw new IllegalArgumentException("Invalid sort criteria: " + sortBy);
        }
        return sortedList;
    }

    @Override
    public void saveToFile() {
        saveRentalAgreements();
    }

    @Override
    public void loadFromFile() {
        loadRentalAgreements();
    }

    @Override
    public void addSubTenant(String agreementId, String subTenantId) {
        RentalAgreement agreement = getRentalAgreement(agreementId);
        Tenant subTenant = tenantManager.getTenant(subTenantId);
        agreement.addSubTenant(subTenant);
        saveRentalAgreements();
    }

    @Override
    public void removeSubTenant(String agreementId, String subTenantId) {
        RentalAgreement agreement = getRentalAgreement(agreementId);
        agreement.removeSubTenant(subTenantId);
        saveRentalAgreements();
    }

    @Override
    public List<RentalAgreement> getActiveRentalAgreements() {
        Date currentDate = new Date();
        return rentalAgreements.values().stream()
                .filter(agreement -> agreement.getEndDate().after(currentDate))
                .collect(Collectors.toList());
    }

    @Override
    public List<RentalAgreement> getExpiredRentalAgreements() {
        Date currentDate = new Date();
        return rentalAgreements.values().stream()
                .filter(agreement -> agreement.getEndDate().before(currentDate))
                .collect(Collectors.toList());
    }

    @Override
    public double getTotalRentalIncome() {
        return rentalAgreements.values().stream()
                .mapToDouble(RentalAgreement::getRentAmount)
                .sum();
    }

    @Override
    public int getTotalActiveAgreements() {
        return getActiveRentalAgreements().size();
    }

    @Override
    public List<RentalAgreement> searchRentalAgreements(String keyword) {
        final String lowercaseKeyword = keyword.toLowerCase();
        return rentalAgreements.values().stream()
                .filter(agreement -> agreement.getAgreementId().toLowerCase().contains(lowercaseKeyword) ||
                        agreement.getProperty().getAddress().toLowerCase().contains(lowercaseKeyword) ||
                        agreement.getMainTenant().getFullName().toLowerCase().contains(lowercaseKeyword))
                .collect(Collectors.toList());
    }

    @Override
    public void extendRentalAgreement(String agreementId, int extensionDays) {
        RentalAgreement agreement = getRentalAgreement(agreementId);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(agreement.getEndDate());
        calendar.add(Calendar.DAY_OF_YEAR, extensionDays);
        agreement.setEndDate(calendar.getTime());
        saveRentalAgreements();
    }

    @Override
    public void terminateRentalAgreement(String agreementId) {
        RentalAgreement agreement = getRentalAgreement(agreementId);
        agreement.setEndDate(new Date());
        agreement.setStatus(RentalAgreement.Status.COMPLETED);
        saveRentalAgreements();
    }

    private void saveRentalAgreements() {
        fileHandler.saveRentalAgreements(new ArrayList<>(rentalAgreements.values()));
    }
}
