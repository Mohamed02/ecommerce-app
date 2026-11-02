package com.ecommerce.product.specification;

import com.ecommerce.product.model.Product;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.util.Locale;

public class ProductSpecification {
    public  static Specification<Product> hasName(String name){
        return (root,query,criteriaBuilder)->
                criteriaBuilder.like(criteriaBuilder.lower(root.get("name")),"%"+name.toLowerCase()+"%");
    }

    public  static  Specification<Product> hasSKU( String sku){
        return (root,query,criteriaBuilder)->
            criteriaBuilder.like(criteriaBuilder.lower(root.get("sku")),"%"+sku.toLowerCase()+"%");
    }
    public static  Specification<Product> hasActive(Boolean active){
        return (root,query,criteriaBuilder)->
                criteriaBuilder.equal(root.get("active"),active);
    }
    public static  Specification<Product> hasMinPrice(BigDecimal minPrice){
        return (root,query,criteriaBuilder)->
                criteriaBuilder.greaterThanOrEqualTo(root.get("price"),minPrice);
    }
    public static  Specification<Product> hasMaxPrice(BigDecimal maxPrice){
        return (root,query,criteriaBuilder)->
                criteriaBuilder.lessThanOrEqualTo(root.get("price"),maxPrice);
    }

}
