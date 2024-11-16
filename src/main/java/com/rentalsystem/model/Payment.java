package com.rentalsystem.model;

import java.util.Date;
import java.util.Objects;

public class Payment {
    private String paymentId;
    private double amount;
    private Date paymentDate;
    private String paymentMethod;
    private RentalAgreement rentalAgreement;
    private Tenant tenant;

    public Payment(String paymentId, double amount, Date paymentDate, String paymentMethod, RentalAgreement rentalAgreement, Tenant tenant) {
        this.paymentId = paymentId;
        this.amount = amount;
        this.paymentDate = paymentDate;
        this.paymentMethod = paymentMethod;
        this.rentalAgreement = rentalAgreement;
        this.tenant = tenant;

        // Update related entities
        rentalAgreement.addPayment(this);
        tenant.addPaymentTransaction(this);
    }

    // Getters and setters for all fields

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Payment payment = (Payment) o;
        return Objects.equals(paymentId, payment.paymentId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(paymentId);
    }

    @Override
    public String toString() {
        return "Payment{" +
                "paymentId='" + paymentId + '\'' +
                ", amount=" + amount +
                ", paymentDate=" + paymentDate +
                ", paymentMethod='" + paymentMethod + '\'' +
                ", rentalAgreement=" + rentalAgreement.getAgreementId() +
                ", tenant=" + tenant.getFullName() +
                '}';
    }
}
