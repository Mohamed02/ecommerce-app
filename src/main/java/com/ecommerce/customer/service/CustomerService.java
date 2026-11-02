package com.ecommerce.customer.service;

import com.ecommerce.customer.dto.CustomerRequest;
import com.ecommerce.customer.dto.CustomerResponse;
import com.ecommerce.customer.model.Customer;
import com.ecommerce.customer.repository.CustomerRepository;
import com.ecommerce.customer.specification.CustomerSpecification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class CustomerService {
    private CustomerRepository customerRepository;

    public  CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

//    public Page<CustomerResponse> getAllCustomers(Pageable pageable) {
//        return this.customerRepository.findAll(pageable).map(this::toCustomerResponse);
//    }

        public  Page<CustomerResponse> serachCustomers(String name, String email, Pageable pageable){

        Specification<Customer> customerSpecification = (root,query,criteriaBuilder)->
                criteriaBuilder.conjunction();

        if(name!=null && !name.isBlank()){
            customerSpecification =  customerSpecification.and(CustomerSpecification.hasName(name));
        }
        if(email!=null && !email.isBlank()){
            customerSpecification =  customerSpecification.and(CustomerSpecification.hasEmail(email));
        }

        return  this.customerRepository.findAll(customerSpecification,pageable).map(this::toCustomerResponse);
    }

    public CustomerResponse getCustomer(Long id) {
        Customer customer =  this.customerRepository.findById(id).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Customer Not Found"));
        return  this.toCustomerResponse(customer);
    }
    public CustomerResponse addCustomer(CustomerRequest customerRequest){
        if(this.customerRepository.existsByEmail(customerRequest.getEmail())){
           throw new ResponseStatusException(HttpStatus.CONFLICT,"Email already exists");
        }
        Customer customer = new Customer(customerRequest.getName(),customerRequest.getEmail(),customerRequest.getPhone());
        Customer savedCustomer = this.customerRepository.save(customer);
        return this.toCustomerResponse(savedCustomer);
    }
    public void deleteCustomer(Long id){
        Customer customer =  this.customerRepository.findById(id).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Customer Not Found"));
        this.customerRepository.deleteById(id);
    }
    public CustomerResponse updateCustomer(Long id, CustomerRequest customerRequest){
        if(this.customerRepository.existsByEmailAndIdNot(customerRequest.getEmail(),id)){
            throw new ResponseStatusException(HttpStatus.CONFLICT,"Email already belongs to another customer");
        }
        Customer customer = this.customerRepository.findById(id).orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND,"Customer Not Found"));
        customer.setEmail(customerRequest.getEmail());
        customer.setPhone(customerRequest.getPhone());
        customer.setName(customerRequest.getName());
        this.customerRepository.save(customer);
        return toCustomerResponse(customer);
    }

    private CustomerResponse toCustomerResponse(Customer customer) {
        return new CustomerResponse(
                customer.getId(),
                customer.getName(),
                customer.getEmail(),
                customer.getPhone());
    }
}
