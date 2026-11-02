package com.ecommerce.customer.controller;

import com.ecommerce.customer.dto.CustomerRequest;
import com.ecommerce.customer.dto.CustomerResponse;
import com.ecommerce.customer.service.CustomerService;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.server.ResponseStatusException;
import tools.jackson.databind.ObjectMapper;

import javax.print.attribute.standard.Media;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CustomerController.class)
public class CustomerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private CustomerService customerService;


    @Test
    void createCustomer_shouldReturn201() throws Exception {
        CustomerRequest request = new CustomerRequest();
        request.setName("John Smith");
        request.setEmail("john@example.com");
        request.setPhone("9876543210");

        CustomerResponse response = new CustomerResponse(
                1L,
                "John Smith",
                "john@example.com",
                "9876543210"
        );
        when(customerService.addCustomer(any(CustomerRequest.class))).thenReturn(response);
        mockMvc.perform(post("/api/customers").contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))).andExpect(status().isCreated()).andDo(print())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("John Smith"))
                .andExpect(jsonPath("$.email").value("john@example.com"));
        verify(customerService).addCustomer(any(CustomerRequest.class));
    }

    @Test
    void createCustomer_shouldReturn400ForInvalidEmail()
            throws Exception {

        CustomerRequest request = new CustomerRequest();
        request.setName("John Smith");
        request.setEmail("not-an-email");
        request.setPhone("9876543210");
        when(customerService.addCustomer(request)).thenThrow( new ResponseStatusException(HttpStatus.CONFLICT,"Email already exists"));
        mockMvc.perform(post("/api/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(customerService, never())
                .addCustomer(any(CustomerRequest.class));
    }
    @Test
    void getCustomerById_shouldReturn200() throws Exception {

        CustomerResponse response = new CustomerResponse(
                1L,
                "John Smith",
                "john@example.com",
                "9876543210"
        );

        when(customerService.getCustomer(1L))
                .thenReturn(response);

        mockMvc.perform(get("/api/customers/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("John Smith"))
                .andExpect(jsonPath("$.email")
                        .value("john@example.com"));

        verify(customerService).getCustomer(1L);
    }

}
