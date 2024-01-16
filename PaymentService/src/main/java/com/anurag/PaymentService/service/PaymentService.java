package com.anurag.PaymentService.service;

import com.anurag.PaymentService.model.PaymentRequest;
import com.anurag.PaymentService.model.PaymentResponse;

public interface PaymentService {
    long doPayment(PaymentRequest paymentRequest);

    PaymentResponse getPaymentDetailsByOrderId(long id);
}
