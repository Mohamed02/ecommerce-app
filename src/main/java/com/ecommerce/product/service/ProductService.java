package com.ecommerce.product.service;

import com.ecommerce.customer.model.Customer;
import com.ecommerce.product.dto.ProductRequest;
import com.ecommerce.product.dto.ProductResponse;
import com.ecommerce.product.model.Product;
import com.ecommerce.product.repository.ProductRepository;
import com.ecommerce.product.specification.ProductSpecification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.List;

@Service
public class ProductService {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public ProductResponse createProduct(ProductRequest request) {

        if (productRepository.existsBySku(request.getSku())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "SKU already exists"
            );
        }

        Product product = new Product(
                request.getName(),
                request.getDescription(),
                request.getSku(),
                request.getPrice(),
                request.getQuantity()
        );

        Product saved = productRepository.save(product);

        return toResponse(saved);
    }
    public ProductResponse getProductById(Long id) {

        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Product not found with id: " + id
                ));

        return toResponse(product);
    }
    public ProductResponse updateProduct(
            Long id, ProductRequest request) {

        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Product not found with id: " + id
                ));

        if (productRepository.existsBySkuAndIdNot(
                request.getSku(), id)) {

            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "SKU already belongs to another product"
            );
        }

        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setSku(request.getSku());
        product.setPrice(request.getPrice());
        product.setQuantity(request.getQuantity());

        Product updated = productRepository.save(product);

        return toResponse(updated);
    }
    public Page<ProductResponse> getAllProducts(Pageable pageable, String name, String sku, Boolean active, BigDecimal minPrice, BigDecimal maxPrice) {

        Specification<Product> productSpecification = ((root, query, criteriaBuilder) -> criteriaBuilder.conjunction());

        if(name!=null && !name.isBlank()){
            productSpecification= productSpecification.and(ProductSpecification.hasName(name));
        }
        if(sku!=null && !sku.isBlank()){
            productSpecification= productSpecification.and(ProductSpecification.hasSKU(sku));
        }
        if(active!=null && (active== true || active ==false)){
            productSpecification= productSpecification.and(ProductSpecification.hasActive(Boolean.valueOf(active)));
        }
        if(minPrice!=null && minPrice.compareTo(BigDecimal.ZERO)>=0){
            productSpecification = productSpecification.and(ProductSpecification.hasMinPrice(minPrice));
        }
        if(maxPrice!=null && maxPrice.compareTo(BigDecimal.ZERO)>=0){
            productSpecification = productSpecification.and(ProductSpecification.hasMaxPrice(maxPrice));
        }

        return productRepository.findAll(productSpecification, pageable)
                .map(this::toResponse);
    }
    public void deleteProduct(Long id) {

        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Product not found with id: " + id
                ));

        productRepository.delete(product);
    }


    private  ProductResponse toResponse(Product product){
        return  new ProductResponse(product.getId(),product.getName(),product.getDescription(),product.getSku(),
        product.getPrice(),product.getQuantity(),product.getActive());
    }


}
