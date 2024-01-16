package com.anurag.ProductService.Service;

import com.anurag.ProductService.Entity.Product;
import com.anurag.ProductService.Exception.ProductServiceCustomException;
import com.anurag.ProductService.Model.ProductRequest;
import com.anurag.ProductService.Model.ProductResponse;
import com.anurag.ProductService.Repository.ProductRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Log4j2
@Service
public class ProductServiceImpl implements ProductService{

    @Autowired
    private ProductRepository productRepository;

    @Override
    public Long addProduct(ProductRequest productRequest) {
        log.info("Adding Product");
        Product product=Product.builder()
                .name(productRequest.getName())
                .quantity(productRequest.getQuantity())
                .price(productRequest.getPrice())
                .build();
        productRepository.save(product);
        return product.getProductId();
    }

    @Override
    public ProductResponse getProductById(long productId) {
        log.info("Get Product By id");
        Product product=productRepository.findById(productId)
                .orElseThrow(()-> new ProductServiceCustomException("Product with given id not found","PRODUCT_NOT_FOUND"));
        ProductResponse productResponse=new ProductResponse();
        BeanUtils.copyProperties(product,productResponse);
        return productResponse;
    }

    @Override
    public void reduceQuantity(long id, long quantity) {
        Product product=productRepository.findById(id)
                .orElseThrow(()-> new ProductServiceCustomException("Product with given id not found","PRODUCT_NOT_FOUND"));
        if(product.getQuantity()<quantity){
            throw new ProductServiceCustomException("Product with the given id does not have sufficient quantity","INSUFFICIENT_QUANTITY");
        }else{
            product.setQuantity(product.getQuantity()-quantity);
            productRepository.save(product);
        }
    }
}
