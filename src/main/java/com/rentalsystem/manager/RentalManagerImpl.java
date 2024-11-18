package com.rentalsystem.manager;

import com.rentalsystem.model.*;
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

    public RentalManagerImpl(FileHandler fileHandler, TenantManager tenantManager, PropertyManager propertyManager, HostManager hostManager, OwnerManager ownerManager) {
        this.fileHandler = fileHandler;
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

            Property property = propertyManager.getProperty(agreement.getProperty().getPropertyId());
            Tenant mainTenant = tenantManager.getTenant(agreement.getMainTenant().getId());
            Host host = hostManager.getHost(agreement.getHost().getId());
            Owner owner = ownerManager.getOwner(agreement.getOwner().getId());

            if (property != null && mainTenant != null && host != null && owner != null) {
                agreement.setProperty(property);
                property.addTenant(mainTenant);
                mainTenant.addRentalAgreement(agreement);
                host.addManagedAgreement(agreement);
                owner.addRentalAgreement(agreement);

                for (Tenant subTenant : agreement.getSubTenants()) {
                    Tenant actualSubTenant = tenantManager.getTenant(subTenant.getId());
                    if (actualSubTenant != null) {
                        agreement.addSubTenant(actualSubTenant);
                        property.addTenant(actualSubTenant);
                    }
                }
            }
        }
    }

    private void updateAgreementStatus(RentalAgreement agreement) {
        Date currentDate = new Date();
        if (agreement.getStartDate().after(currentDate)) {
            agreement.setStatus(RentalAgreement.Status.NEW);
        } else if (agreement.getEndDate().before(currentDate)) {
            agreement.setStatus(RentalAgreement.Status.COMPLETED);
        } else {
            agreement.setStatus(RentalAgreement.Status.ACTIVE);
        }
    }


    @Override
    public void addRentalAgreement(RentalAgreement agreement) {
        if (rentalAgreements.containsKey(agreement.getAgreementId())) {
            throw new IllegalArgumentException("Rental agreement with ID " + agreement.getAgreementId() + " already exists.");
        }
        rentalAgreements.put(agreement.getAgreementId(), agreement);
        updateAgreementStatus(agreement);

        agreement.getProperty().addTenant(agreement.getMainTenant());
        agreement.getMainTenant().addRentalAgreement(agreement);
        agreement.getHost().addManagedAgreement(agreement);
        agreement.getOwner().addRentalAgreement(agreement);

        for (Tenant subTenant : agreement.getSubTenants()) {
            agreement.getProperty().addTenant(subTenant);
            subTenant.addRentalAgreement(agreement);
        }

        saveRentalAgreements();
    }

    @Override
    public void updateRentalAgreement(RentalAgreement agreement) {
        RentalAgreement existingAgreement = rentalAgreements.get(agreement.getAgreementId());
        if (existingAgreement == null) {
            throw new IllegalArgumentException("Rental agreement with ID " + agreement.getAgreementId() + " does not exist.");
        }

        existingAgreement.getProperty().removeTenant(existingAgreement.getMainTenant());
        existingAgreement.getMainTenant().removeRentalAgreement(existingAgreement);
        existingAgreement.getHost().removeManagedAgreement(existingAgreement);
        existingAgreement.getOwner().removeRentalAgreement(existingAgreement);

        for (Tenant subTenant : existingAgreement.getSubTenants()) {
            existingAgreement.getProperty().removeTenant(subTenant);
            subTenant.removeRentalAgreement(existingAgreement);
        }
        updateAgreementStatus(agreement);

        agreement.getProperty().addTenant(agreement.getMainTenant());
        agreement.getMainTenant().addRentalAgreement(agreement);
        agreement.getHost().addManagedAgreement(agreement);
        agreement.getOwner().addRentalAgreement(agreement);

        for (Tenant subTenant : agreement.getSubTenants()) {
            agreement.getProperty().addTenant(subTenant);
            subTenant.addRentalAgreement(agreement);
        }

        rentalAgreements.put(agreement.getAgreementId(), agreement);
        saveRentalAgreements();
    }

    @Override
    public void deleteRentalAgreement(String agreementId) {
        RentalAgreement agreement = rentalAgreements.remove(agreementId);
        if (agreement == null) {
            throw new IllegalArgumentException("Rental agreement with ID " + agreementId + " does not exist.");
        }

        agreement.getProperty().removeTenant(agreement.getMainTenant());
        agreement.getMainTenant().removeRentalAgreement(agreement);
        agreement.getHost().removeManagedAgreement(agreement);
        agreement.getOwner().removeRentalAgreement(agreement);

        for (Tenant subTenant : agreement.getSubTenants()) {
            agreement.getProperty().removeTenant(subTenant);
            subTenant.removeRentalAgreement(agreement);
        }

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
            case "propertyid":
                sortedList.sort(Comparator.comparing(a -> a.getProperty().getPropertyId()));
                break;
            case "tenantname":
                sortedList.sort(Comparator.comparing(a -> a.getMainTenant().getFullName()));
                break;
            case "ownername":
                sortedList.sort(Comparator.comparing(a -> a.getOwner().getFullName()));
                break;
            case "hostname":
                sortedList.sort(Comparator.comparing(a -> a.getHost().getFullName()));
                break;
            case "startdate":
                sortedList.sort(Comparator.comparing(RentalAgreement::getStartDate));
                break;
            case "enddate":
                sortedList.sort(Comparator.comparing(RentalAgreement::getEndDate));
                break;
            case "rentamount":
                sortedList.sort(Comparator.comparing(RentalAgreement::getRentAmount));
                break;
            case "status":
                sortedList.sort(Comparator.comparing(RentalAgreement::getStatus));
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
        if (subTenant == null) {
            throw new IllegalArgumentException("Tenant with ID " + subTenantId + " does not exist.");
        }
        agreement.addSubTenant(subTenant);
        agreement.getProperty().addTenant(subTenant);
        subTenant.addRentalAgreement(agreement);
        saveRentalAgreements();
    }

    @Override
    public void removeSubTenant(String agreementId, String subTenantId) {
        RentalAgreement agreement = getRentalAgreement(agreementId);
        Tenant subTenant = tenantManager.getTenant(subTenantId);
        if (subTenant == null) {
            throw new IllegalArgumentException("Tenant with ID " + subTenantId + " does not exist.");
        }
        agreement.removeSubTenant(subTenantId);
        agreement.getProperty().removeTenant(subTenant);
        subTenant.removeRentalAgreement(agreement);
        saveRentalAgreements();
    }

    @Override
    public List<RentalAgreement> getActiveRentalAgreements() {
        Date currentDate = new Date();
        return rentalAgreements.values().stream()
                .filter(agreement -> agreement.getEndDate().after(currentDate) && agreement.getStatus() == RentalAgreement.Status.ACTIVE)
                .collect(Collectors.toList());
    }

    @Override
    public List<RentalAgreement> getExpiredRentalAgreements() {
        Date currentDate = new Date();
        return rentalAgreements.values().stream()
                .filter(agreement -> agreement.getEndDate().before(currentDate) || agreement.getStatus() == RentalAgreement.Status.COMPLETED)
                .collect(Collectors.toList());
    }

    @Override
    public double getTotalRentalIncome() {
        return rentalAgreements.values().stream()
                .filter(agreement -> agreement.getStatus() == RentalAgreement.Status.ACTIVE)
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
                        agreement.getMainTenant().getFullName().toLowerCase().contains(lowercaseKeyword) ||
                        agreement.getOwner().getFullName().toLowerCase().contains(lowercaseKeyword) ||
                        agreement.getHost().getFullName().toLowerCase().contains(lowercaseKeyword))
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

        agreement.getProperty().removeTenant(agreement.getMainTenant());
        for (Tenant subTenant : agreement.getSubTenants()) {
            agreement.getProperty().removeTenant(subTenant);
        }

        saveRentalAgreements();
    }

    private void saveRentalAgreements() {
        fileHandler.saveRentalAgreements(new ArrayList<>(rentalAgreements.values()));
    }
}
