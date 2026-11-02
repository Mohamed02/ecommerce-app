package com.ecommerce.product.controller;

import com.ecommerce.product.dto.ProductRequest;
import com.ecommerce.product.dto.ProductResponse;
import com.ecommerce.product.model.Product;
import com.ecommerce.product.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api")
public class ProductController {

    private ProductService productService;
    ProductController(ProductService productService){
        this.productService= productService;
    }

    @GetMapping("/products")
    public Page<ProductResponse> getAllProducts(
            @RequestParam(required = false)  String name,
            @RequestParam(required = false) String sku,
            @RequestParam(required = false) Boolean active,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @PageableDefault(page = 0,size = 10, sort = "id", direction = Sort.Direction.ASC) Pageable pageable){
        return  productService.getAllProducts(pageable, name, sku, active, minPrice, maxPrice);
    }
    @PostMapping("/products")
    public ProductResponse createProduct(
            @Valid @RequestBody ProductRequest request) {

        return productService.createProduct(request);
    }

    @GetMapping("/products/{id}")
    public ProductResponse getProductById(
            @PathVariable Long id) {
        return productService.getProductById(id);
    }
    @PutMapping("/products/{id}")
    public ProductResponse updateProduct(
            @PathVariable Long id,
            @Valid @RequestBody ProductRequest request) {

        return productService.updateProduct(id, request);
    }
    @DeleteMapping("/products/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
    }

}
