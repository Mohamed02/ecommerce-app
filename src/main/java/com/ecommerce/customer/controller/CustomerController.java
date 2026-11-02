package com.ecommerce.customer.controller;

import com.ecommerce.customer.dto.CustomerRequest;
import com.ecommerce.customer.dto.CustomerResponse;
import com.ecommerce.customer.service.CustomerService;
import jakarta.validation.Valid;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Log4j2
@RestController
@RequestMapping("/api")
public class CustomerController {
    private CustomerService customerService;

    public CustomerController(CustomerService customerService){
        this.customerService = customerService;
    };

    @GetMapping("/customers")
    public ResponseEntity<Page<CustomerResponse>> getAllCustomers(@RequestParam(required = false) String name, @RequestParam(required = false) String email,@PageableDefault(page = 0,size = 10,sort = "id", direction = Sort.Direction.ASC) Pageable pageable){
        return new ResponseEntity<>(this.customerService.serachCustomers(name, email, pageable), HttpStatus.OK);
    }

    @GetMapping("/customers/{id}")
    public  ResponseEntity<CustomerResponse> getCustomer(@PathVariable Long id){
        return  new ResponseEntity<>(this.customerService.getCustomer(id), HttpStatus.OK);
    }
    @PostMapping("/customers")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<CustomerResponse> addCustomer(@Valid @RequestBody CustomerRequest request){
            return  new ResponseEntity<>(this.customerService.addCustomer(request), HttpStatus.CREATED);
    }
    @DeleteMapping("/customers/{id}")
    public ResponseEntity<String> deleteCustomer(@PathVariable Long id){
            this.customerService.deleteCustomer(id);
            return new ResponseEntity<>("Deleted succesfully", HttpStatus.NO_CONTENT);
    };

    @PostMapping("/customers/{id}")
    public ResponseEntity<CustomerResponse> updateCustomer(@PathVariable Long id, @Valid @RequestBody CustomerRequest customerRequest){
       return new ResponseEntity<>(this.customerService.updateCustomer(id,customerRequest), HttpStatus.OK);
    }

}
