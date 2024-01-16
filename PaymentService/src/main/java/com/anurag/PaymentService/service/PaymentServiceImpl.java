package com.anurag.PaymentService.service;

import com.anurag.PaymentService.entity.TransactionDetails;
import com.anurag.PaymentService.model.PaymentMode;
import com.anurag.PaymentService.model.PaymentRequest;
import com.anurag.PaymentService.model.PaymentResponse;
import com.anurag.PaymentService.repository.PaymentRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Log4j2
@Service
public class PaymentServiceImpl implements PaymentService {

    @Autowired
    PaymentRepository paymentRepository;

    @Override
    public long doPayment(PaymentRequest paymentRequest) {
        log.info("Recording payment information",paymentRequest);
        TransactionDetails transactionDetails=TransactionDetails.builder()
                .paymentDate(Instant.now())
                .paymentMode(paymentRequest.getPaymentMode().name())
                .paymentStatus("SUCCESS")
                .amount(paymentRequest.getAmount())
                .orderId(paymentRequest.getOrderId())
                .referenceNumber(paymentRequest.getReferenceNumber())
                .build();
        paymentRepository.save(transactionDetails);
        log.info("Transaction completed with id:",transactionDetails.getId());
        return transactionDetails.getId();
    }

    @Override
    public PaymentResponse getPaymentDetailsByOrderId(long orderId) {
        log.info("Getting payment details for orderId:"+orderId);
        TransactionDetails transactionDetails=paymentRepository.findByOrderId(orderId);

        PaymentResponse paymentResponse=PaymentResponse.builder()
                .paymentId(transactionDetails.getId())
                .orderId(transactionDetails.getOrderId())
                .amount(transactionDetails.getAmount())
                .paymentDate(transactionDetails.getPaymentDate())
                .paymentMode(PaymentMode.valueOf(transactionDetails.getPaymentMode()))
                .paymentStatus(transactionDetails.getPaymentStatus())
                .build();

        return paymentResponse;
    }
}
