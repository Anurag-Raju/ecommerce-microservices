package com.anurag.PaymentService.controller;

import com.anurag.PaymentService.model.PaymentRequest;
import com.anurag.PaymentService.model.PaymentResponse;
import com.anurag.PaymentService.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/payment")
public class PaymentController {

    @Autowired
    PaymentService paymentService;

    @PostMapping
    public ResponseEntity<Long> doPayment(@RequestBody PaymentRequest paymentRequest){
        return new ResponseEntity<>(paymentService.doPayment(paymentRequest),HttpStatus.OK);
    }

    @GetMapping("/order/{id}")
    public ResponseEntity<PaymentResponse> getPaymentDetailsByOrderId(@PathVariable long id){
        return new ResponseEntity<>(paymentService.getPaymentDetailsByOrderId(id),HttpStatus.OK);
    }
}
