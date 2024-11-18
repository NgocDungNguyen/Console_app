package com.rentalsystem.model;

import java.util.Date;

public class Payment {
    private String paymentId;
    private RentalAgreement rentalAgreement;
    private Tenant tenant;
    private Date paymentDate;
    private double amount;
    private String paymentMethod;

    public Payment(String paymentId, RentalAgreement rentalAgreement, Tenant tenant, Date paymentDate, double amount, String paymentMethod) {
        this.paymentId = paymentId;
        this.rentalAgreement = rentalAgreement;
        this.tenant = tenant;
        this.paymentDate = paymentDate;
        this.amount = amount;
        this.paymentMethod = paymentMethod;
    }

    public String getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(String paymentId) {
        this.paymentId = paymentId;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public Date getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(Date paymentDate) {
        this.paymentDate = paymentDate;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public RentalAgreement getRentalAgreement() {
        return rentalAgreement;
    }

    public void setRentalAgreement(RentalAgreement rentalAgreement) {
        this.rentalAgreement = rentalAgreement;
    }

    public Tenant getTenant() {
        return tenant;
    }

    public void setTenant(Tenant tenant) {
        this.tenant = tenant;
    }

    @Override
    public String toString() {
        return "Payment{" +
                "paymentId='" + paymentId + '\'' +
                ", amount=" + amount +
                ", paymentDate=" + paymentDate +
                ", paymentMethod='" + paymentMethod + '\'' +
                ", rentalAgreement=" + rentalAgreement.getAgreementId() +
                ", tenant=" + tenant.getId() +
                '}';
    }
}
