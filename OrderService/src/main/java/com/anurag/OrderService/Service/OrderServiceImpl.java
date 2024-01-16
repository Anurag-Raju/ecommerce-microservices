package com.anurag.OrderService.Service;

import com.anurag.OrderService.Entity.OrderDetails;
import com.anurag.OrderService.Model.OrderRequest;
import com.anurag.OrderService.Model.OrderResponse;
import com.anurag.OrderService.Repository.OrderRepository;
import com.anurag.OrderService.exception.CustomException;
import com.anurag.OrderService.external.client.PaymentService;
import com.anurag.OrderService.external.client.ProductService;
import com.anurag.OrderService.external.request.PaymentRequest;
import com.anurag.OrderService.external.response.PaymentResponse;
import com.anurag.OrderService.external.response.ProductResponse;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;

@Log4j2
@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    OrderRepository orderRepository;
    @Autowired
    ProductService productService;

    @Autowired
    PaymentService paymentService;

    @Autowired
    RestTemplate restTemplate;

    @Override
    public Long placeOrder(OrderRequest orderRequest) {
        productService.reduceQuantity(orderRequest.getProductId(), orderRequest.getQuantity());
        OrderDetails order = OrderDetails.builder()
                .productId(orderRequest.getProductId())
                .quantity(orderRequest.getQuantity())
                .amount(orderRequest.getTotalAmount())
                .orderStatus("CREATED")
                .orderDate(Instant.now())
                .build();
        orderRepository.save(order);
        log.info("doing payment");
        PaymentRequest paymentRequest = PaymentRequest.builder()
                .orderId(order.getId())
                .paymentMode(orderRequest.getPaymentMode())
                .amount(orderRequest.getTotalAmount())
                .build();
        String orderStatus = null;
        try {
            paymentService.doPayment(paymentRequest);
            log.info("Payment done Successfully,Changing the order status to PLACED");
            orderStatus = "PLACED";
        } catch (Exception e) {
            log.error("Payment Failed");
            orderStatus = "PAYMENT_FAILED";
        }
        order.setOrderStatus(orderStatus);
        orderRepository.save(order);
        log.info("Order placed with order id:" + order.getId());
        return order.getId();
    }

    @Override
    public OrderResponse getOrderDetails(long id) {
        log.info("Getting order details for order id:"+ id);
        OrderDetails orderDetails = orderRepository.findById(id)
                .orElseThrow(() -> new CustomException("Order not found with id" + id, "NOT_FOUND", 404));

        log.info("Invoking to get product details for product id -> " + orderDetails.getProductId());

        ProductResponse productResponse=restTemplate
                .getForObject("http://PRODUCT-SERVICE/product/"+orderDetails.getProductId(),
                        ProductResponse.class);
//
        log.info("Getting payment response for order");

        PaymentResponse paymentResponse=restTemplate
                .getForObject("http://PAYMENT-SERVICE/payment/order/"+orderDetails.getId(),
                        PaymentResponse.class);
//
        OrderResponse.ProductDetails productDetails=OrderResponse.ProductDetails
                .builder()
                .name(productResponse.getName())
                .productId(productResponse.getProductId())
                .build();
        OrderResponse.PaymentDetails paymentDetails=OrderResponse.PaymentDetails
                .builder()
                .paymentId(paymentResponse.getPaymentId())
                .paymentStatus(paymentResponse.getPaymentStatus())
                .paymentDate(paymentResponse.getPaymentDate())
                .paymentMode(paymentResponse.getPaymentMode())
                .build();

        OrderResponse orderResponse = OrderResponse.builder()
                .orderId(orderDetails.getId())
                .amount(orderDetails.getAmount())
                .orderStatus(orderDetails.getOrderStatus())
                .orderDate(orderDetails.getOrderDate())
                .productDetails(productDetails)
                .paymentDetails(paymentDetails)
                .build();
        return orderResponse;
    }
}
