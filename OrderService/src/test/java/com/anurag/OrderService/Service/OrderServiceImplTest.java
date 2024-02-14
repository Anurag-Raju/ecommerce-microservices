package com.anurag.OrderService.Service;

import com.anurag.OrderService.Entity.OrderDetails;
import com.anurag.OrderService.Model.OrderRequest;
import com.anurag.OrderService.Model.OrderResponse;
import com.anurag.OrderService.Model.PaymentMode;
import com.anurag.OrderService.Repository.OrderRepository;
import com.anurag.OrderService.exception.CustomException;
import com.anurag.OrderService.external.client.PaymentService;
import com.anurag.OrderService.external.client.ProductService;
import com.anurag.OrderService.external.request.PaymentRequest;
import com.anurag.OrderService.external.response.PaymentResponse;
import com.anurag.OrderService.external.response.ProductResponse;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;
import java.util.Optional;

@SpringBootTest
 public class OrderServiceImplTest {

    @Mock
    OrderRepository orderRepository;
    @Mock
    ProductService productService;

    @Mock
    PaymentService paymentService;

    @Mock
    RestTemplate restTemplate;

    @InjectMocks
    OrderService orderService=new OrderServiceImpl();

    @DisplayName("Get Order Success Scenario")
    @Test
    void test_When_Order_Is_Success(){
        //Mocking
       OrderDetails orderDetails=getMockOrder();
       Mockito.when(orderRepository.findById(ArgumentMatchers.anyLong()))
               .thenReturn(Optional.of(orderDetails));
       Mockito.when(restTemplate
                .getForObject("http://PRODUCT-SERVICE/product/"+orderDetails.getProductId(),
                        ProductResponse.class)).thenReturn(getMockProductResponse());
       Mockito.when(restTemplate
               .getForObject("http://PAYMENT-SERVICE/payment/order/"+orderDetails.getId(),
                       PaymentResponse.class)).thenReturn(getMockPaymentResponse());

       //Actual
       OrderResponse orderResponse=orderService.getOrderDetails(1);

       //Verification
       Mockito.verify(orderRepository,Mockito.times(1))
               .findById(ArgumentMatchers.anyLong());
       Mockito.verify(restTemplate,Mockito.times(1))
               .getForObject("http://PRODUCT-SERVICE/product/"+orderDetails.getProductId(),
                       ProductResponse.class);
        Mockito.verify(restTemplate,Mockito.times(1))
                .getForObject("http://PAYMENT-SERVICE/payment/order/"+orderDetails.getId(),
                        PaymentResponse.class);

        //Assert
        Assertions.assertNotNull(orderResponse);
        Assertions.assertEquals(orderDetails.getId(),orderResponse.getOrderId());
    }

    @DisplayName("Get Orders - Failed Scenario")
    @Test
    void test_For_Order_Not_Found_NotFound(){
       //Mocking
       Mockito.when(orderRepository.findById(ArgumentMatchers.anyLong()))
               .thenReturn(Optional.ofNullable(null));
       //Actual
//       OrderResponse orderResponse=orderService.getOrderDetails(1);
       CustomException exception= Assertions.assertThrows(CustomException.class,
               ()->orderService.getOrderDetails(1));

       Assertions.assertEquals("NOT_FOUND",exception.getErrorCode());
       Assertions.assertEquals(404,exception.getStatus());

       Mockito.verify(orderRepository,Mockito.times(1))
               .findById(ArgumentMatchers.anyLong());
    }

    @DisplayName("Place Order-Success Scenario")
    @Test
    void test_For_Place_Order_Success(){
       OrderDetails orderDetails=getMockOrder();
       OrderRequest orderRequest=getOrderRequest();
      long orderId=orderService.placeOrder(orderRequest);

      Mockito.when(orderRepository.save(ArgumentMatchers.any(OrderDetails.class)))
              .thenReturn(orderDetails);
      Mockito.when(productService.reduceQuantity(ArgumentMatchers.anyLong(),ArgumentMatchers.anyLong()))
              .thenReturn(new ResponseEntity<Void>(HttpStatus.OK));
      Mockito.when(paymentService.doPayment(ArgumentMatchers.any(PaymentRequest.class)))
              .thenReturn(new ResponseEntity<Long>(1L,HttpStatus.OK));

      Mockito.verify(orderRepository,Mockito.times(2))
              .save(ArgumentMatchers.any());

      Mockito.verify(productService,Mockito.times(1))
              .reduceQuantity(ArgumentMatchers.anyLong(),ArgumentMatchers.anyLong());

      Mockito.verify(paymentService,Mockito.times(1))
              .doPayment(ArgumentMatchers.any(PaymentRequest.class));

//      Assertions.assertEquals(orderDetails.getId(),orderId);

    }

    @DisplayName("Place_Order_Payment Failed Scenario")
    @Test
    void test_Place_Order_Payment_Fails_PlaceOrder(){
       OrderDetails orderDetails=getMockOrder();
       OrderRequest orderRequest=getOrderRequest();
       long orderId=orderService.placeOrder(orderRequest);

       Mockito.when(orderRepository.save(ArgumentMatchers.any(OrderDetails.class)))
               .thenReturn(orderDetails);
       Mockito.when(productService.reduceQuantity(ArgumentMatchers.anyLong(),ArgumentMatchers.anyLong()))
               .thenReturn(new ResponseEntity<Void>(HttpStatus.OK));
       Mockito.when(paymentService.doPayment(ArgumentMatchers.any(PaymentRequest.class)))
               .thenThrow(new RuntimeException());

       Mockito.verify(orderRepository,Mockito.times(2))
               .save(ArgumentMatchers.any());

       Mockito.verify(productService,Mockito.times(1))
               .reduceQuantity(ArgumentMatchers.anyLong(),ArgumentMatchers.anyLong());

       Mockito.verify(paymentService,Mockito.times(1))
               .doPayment(ArgumentMatchers.any(PaymentRequest.class));
    }

   private OrderRequest getOrderRequest() {
       return OrderRequest.builder()
               .paymentMode(PaymentMode.CASH)
               .productId(1)
               .quantity(10)
               .totalAmount(100)
               .build();
   }

   private PaymentResponse getMockPaymentResponse() {
        return PaymentResponse.builder()
                .orderId(1)
                .paymentId(1)
                .paymentMode(PaymentMode.CASH)
                .paymentDate(Instant.now())
                .paymentStatus("ACCEPTED")
                .amount(100)
                .build();
    }

    private ProductResponse getMockProductResponse() {
        return ProductResponse.builder()
                .name("IPhone")
                .productId(2)
                .price(100)
                .quantity(200)
                .build();
    }

    private OrderDetails getMockOrder() {
       return OrderDetails.builder()
               .orderStatus("SUCCESS")
               .orderDate(Instant.now())
               .amount(100)
               .id(1)
               .quantity(200)
               .productId(2)
               .build();
   }
}