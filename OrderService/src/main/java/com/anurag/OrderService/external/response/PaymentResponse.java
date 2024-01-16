package com.anurag.OrderService.external.response;

import com.anurag.OrderService.Model.PaymentMode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PaymentResponse {
    private long paymentId;
    private String paymentStatus;
    private long orderId;
    private long amount;
    private Instant paymentDate;
    private PaymentMode paymentMode;
}
