package com.example.payment;

public interface PaymentService {
    PaymentApiResponse charge(double amount);
}