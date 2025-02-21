package com.example.payment;

public class PaymentProcessor {
    private final PaymentService paymentService;
    private final DatabaseService databaseService;
    private final EmailService emailService;

    public PaymentProcessor(PaymentService paymentService, DatabaseService databaseService, EmailService emailService) {
        this.paymentService = paymentService;
        this.databaseService = databaseService;
        this.emailService = emailService;
    }

        public boolean processPayment(double amount) {
            if (amount <= 0) {
                throw new IllegalArgumentException("Amount must be greater than zero");
            }

            PaymentApiResponse response = paymentService.charge(amount);
            if (response.isSuccess()) {
                databaseService.savePayment(amount, "SUCCESS");
                emailService.sendPaymentConfirmation("user@example.com", amount);
            }

            return response.isSuccess();
        }
}