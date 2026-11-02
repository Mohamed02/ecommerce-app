package com.ecommerce.customer.service;


import com.ecommerce.customer.dto.CustomerRequest;
import com.ecommerce.customer.dto.CustomerResponse;
import com.ecommerce.customer.model.Customer;
import com.ecommerce.customer.repository.CustomerRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {

    @Mock
    private CustomerRepository customerRepository;

    @InjectMocks
    private CustomerService customerService;

    @Test
    void createCustomer_shouldReturnSavedCustomer(){
        CustomerRequest request = new CustomerRequest();
        request.setName("john");
        request.setEmail("john@example.com");
        request.setPhone("9487367");

        when(customerRepository.existsByEmail("john@example.com")).thenReturn(false);

        when(customerRepository.save(any(Customer.class))).thenAnswer(invocation -> {
            Customer customer = invocation.getArgument(0);
            customer.setId(1L);
            return customer;
        });

        CustomerResponse response  = customerService.addCustomer(request);
        assertEquals(1L,response.getId());
        assertEquals("john",response.getName());
        assertEquals("john@example.com",response.getEmail());
        assertEquals("9487367",response.getPhone());
        verify(customerRepository).save(any(Customer.class));
    }

    @Test
    void getCustomerById_shouldReturnCustomer(){
        Customer customer = new Customer();
        customer.setId(1L);
        customer.setName("john");
        customer.setEmail("john@example.com");
        customer.setPhone("9487367");
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));

        CustomerResponse  customerResponse= customerService.getCustomer(1L);
        assertEquals("john", customerResponse.getName());
        assertEquals("john@example.com", customerResponse.getEmail());
        assertEquals("9487367", customerResponse.getPhone());
        verify(customerRepository).findById(1L);
    }
    @Test
    void getCustomerById_shouldThrowExceptionWhenNotFound() {
        when(customerRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(
                ResponseStatusException.class, ()->customerService.getCustomer(1L)
        );
        verify(customerRepository).findById(1L);
    }

    @Test
    void createCustomer_shouldRejectDuplicateEmail() {
        CustomerRequest request = new CustomerRequest();
        request.setName("John Smith");
        request.setEmail("john@example.com");
        request.setPhone("9876543210");

        when(customerRepository.existsByEmail("john@example.com")).thenReturn(true);
        assertThrows(ResponseStatusException.class,()->customerService.addCustomer(request));


    }
}
