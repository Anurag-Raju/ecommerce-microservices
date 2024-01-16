package com.anurag.OrderService.Controller;

import com.anurag.OrderService.Model.OrderRequest;
import com.anurag.OrderService.Model.OrderResponse;
import com.anurag.OrderService.Service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/orders")
public class OrderController {

    @Autowired
    OrderService orderService;

    @PostMapping("/placeOrder")
    public ResponseEntity<Long> placeOrder(@RequestBody OrderRequest orderRequest){
        Long id=orderService.placeOrder(orderRequest);

        return new ResponseEntity<>(id, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public  ResponseEntity<OrderResponse> getOrderDetails(@PathVariable long id){
        return new ResponseEntity<>(orderService.getOrderDetails(id),HttpStatus.OK);
    }
}
