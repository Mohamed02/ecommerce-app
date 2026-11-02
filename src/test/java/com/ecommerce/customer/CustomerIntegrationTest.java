package com.ecommerce.customer;

import com.ecommerce.customer.dto.CustomerRequest;
import com.ecommerce.customer.repository.CustomerRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class CustomerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private CustomerRepository customerRepository;

    @BeforeEach
    void cleanDatabase() {
        customerRepository.deleteAll();
    }
    @Test
    void createCustomerAndFetchById() throws Exception{
        String email = "john.integration@example.com";
        String phone = "9876543210";
        String name = "John Smith";
        CustomerRequest request = new CustomerRequest();
        request.setName(name);
        request.setEmail(email);
        request.setPhone(phone);

        String response = mockMvc.perform(post("/api/customers").contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))).andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value(name))
                .andExpect(jsonPath("$.email").value(email))
                .andExpect(jsonPath("$.phone").value(phone))
                .andReturn().getResponse().getContentAsString();

        Long customerId = objectMapper.readTree(response).get("id").asLong();
        mockMvc.perform(get("/api/customers/"+customerId)).andExpect(jsonPath("$.name").value("John Smith"))
                .andExpect(jsonPath("$.email").value(email))
                .andExpect(jsonPath("$.phone").value(phone));

    }

    @Test
    void createCustomerWithDuplicateEmailShouldReturn409()
            throws Exception {

        CustomerRequest request = new CustomerRequest();

        request.setName("John Smith");
        request.setEmail("duplicate@example.com");
        request.setPhone("9876543210");

        // First request
        mockMvc.perform(post("/api/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());

        // Second request with the same email
        request.setName("Another Customer");

        mockMvc.perform(post("/api/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict());
    }
}
