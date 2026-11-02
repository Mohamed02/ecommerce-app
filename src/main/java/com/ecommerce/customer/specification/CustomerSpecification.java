package com.ecommerce.customer.specification;

import com.ecommerce.customer.model.Customer;
import org.springframework.data.jpa.domain.Specification;

import java.util.Locale;

public class CustomerSpecification {
    public  static Specification<Customer> hasName(String name){
        return ((root, query, criteriaBuilder) ->
                criteriaBuilder.like(criteriaBuilder.lower(root.get("name")),"%"+name.toLowerCase()+"%")) ;
    }
    public static Specification<Customer> hasEmail(String email){
        return  ((root,query,criteriaBuilder)->
                criteriaBuilder.equal(criteriaBuilder.lower(root.get("email")),email.toLowerCase()) );
    }
}
