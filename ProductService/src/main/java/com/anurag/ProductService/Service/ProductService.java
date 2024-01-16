package com.anurag.ProductService.Service;

import com.anurag.ProductService.Model.ProductRequest;
import com.anurag.ProductService.Model.ProductResponse;
import org.springframework.stereotype.Service;

public interface ProductService {
    Long addProduct(ProductRequest productRequest);

    ProductResponse getProductById(long productId);

    void reduceQuantity(long id, long quantity);
}
