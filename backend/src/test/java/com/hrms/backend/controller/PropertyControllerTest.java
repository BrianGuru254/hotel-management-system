package com.hrms.backend.controller;

import com.hrms.backend.dto.PropertyResponse;
import com.hrms.backend.exception.InvalidRelationshipException;
import com.hrms.backend.service.PropertyService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;

@WebMvcTest(PropertyController.class)
class PropertyControllerTest {
    @Autowired MockMvc mvc;
    @MockitoBean PropertyService service;

    @Test void returnsCreatedForValidPropertyRequest() throws Exception {
        UUID id = UUID.randomUUID();
        when(service.create(any())).thenReturn(PropertyResponse.builder().id(id).name("Riverside Hotel").build());

        mvc.perform(post("/api/v1/properties").with(jwt().jwt(token -> token.subject(UUID.randomUUID().toString())))
                        .contentType("application/json")
                        .content("{\"organizationId\":\"" + UUID.randomUUID() + "\",\"branchId\":\"" + UUID.randomUUID() + "\",\"name\":\"Riverside Hotel\",\"code\":\"H1\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(id.toString()))
                .andExpect(jsonPath("$.name").value("Riverside Hotel"));
    }

    @Test void mapsOwnershipMismatchToUnprocessableEntity() throws Exception {
        when(service.create(any())).thenThrow(new InvalidRelationshipException("The branch does not belong to the supplied organization"));

        mvc.perform(post("/api/v1/properties").with(jwt().jwt(token -> token.subject(UUID.randomUUID().toString())))
                        .contentType("application/json")
                        .content("{\"organizationId\":\"" + UUID.randomUUID() + "\",\"branchId\":\"" + UUID.randomUUID() + "\",\"name\":\"Riverside Hotel\",\"code\":\"H1\"}"))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.detail").value("The branch does not belong to the supplied organization"));
    }
}
