package com.example.payment;

public class ExternalPaymentService implements PaymentService {

    private static final String API_KEY = "sk_test_123456";

    @Override
    public PaymentApiResponse charge(double amount) {
        return PaymentApi.charge(API_KEY, amount);
    }
}
