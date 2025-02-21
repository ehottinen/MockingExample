package com.example.payment;

public class PaymentApiResponse {
    private final boolean success;
    private final String message;

    public PaymentApiResponse(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }
}
