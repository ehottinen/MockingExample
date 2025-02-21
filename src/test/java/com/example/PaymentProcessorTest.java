package com.example;

import com.example.payment.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class PaymentProcessorTest {

    private PaymentService paymentService;
    private DatabaseService databaseService;
    private EmailService emailService;
    private PaymentProcessor paymentProcessor;

    @BeforeEach
    void setUp() {
        paymentService = mock(PaymentService.class);
        databaseService = mock(DatabaseService.class);
        emailService = mock(EmailService.class);
        paymentProcessor = new PaymentProcessor(paymentService, databaseService, emailService);
    }

    @Test
    void processPayment_shouldReturnTrueWhenPaymentSucceeds() {
        when(paymentService.charge(100.0)).thenReturn(new PaymentApiResponse(true, "Payment successful"));

        boolean result = paymentProcessor.processPayment(100.0);

        assertThat(result).isTrue();
        verify(paymentService).charge(100.0);
        verify(databaseService).savePayment(100.0, "SUCCESS");
        verify(emailService).sendPaymentConfirmation("user@example.com", 100.0);
    }

    @Test
    void processPayment_shouldReturnFalseWhenPaymentFails() {
        when(paymentService.charge(100.0)).thenReturn(new PaymentApiResponse(false, "Payment failed"));

        boolean result = paymentProcessor.processPayment(100.0);

        assertThat(result).isFalse();
        verify(paymentService).charge(100.0);
        verify(databaseService, never()).savePayment(anyDouble(), anyString());
        verify(emailService, never()).sendPaymentConfirmation(anyString(), anyDouble());
    }

    @Test
    void processPayment_shouldThrowExceptionForNegativeAmount() {
        assertThatThrownBy(() -> paymentProcessor.processPayment(-50.0))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Amount must be greater than zero");
    }
}
