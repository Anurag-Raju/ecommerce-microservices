package com.anurag.OrderService.Service;

import com.anurag.OrderService.Model.OrderRequest;
import com.anurag.OrderService.Model.OrderResponse;

public interface OrderService {
    Long placeOrder(OrderRequest orderRequest);

    OrderResponse getOrderDetails(long id);
}
