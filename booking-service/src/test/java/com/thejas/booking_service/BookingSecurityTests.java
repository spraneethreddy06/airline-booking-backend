package com.thejas.booking_service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = "payment.service.secret=valid-secret")
@ActiveProfiles("test")
@AutoConfigureMockMvc
class BookingSecurityTests {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void adminEndpointRejectsUnauthenticatedRequest() throws Exception {
        mockMvc.perform(get("/api/admin/bookings"))
                .andExpect(status().isForbidden());
    }

    @Test
    void internalEndpointRejectsMissingServiceSecret() throws Exception {
        mockMvc.perform(put("/api/admin/bookings/internal/1/status").param("status", "CONFIRMED"))
                .andExpect(status().isForbidden());
    }
}
