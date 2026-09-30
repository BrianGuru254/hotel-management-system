package com.hrms.backend.controller;

import com.hrms.backend.dto.OrganizationResponse;
import com.hrms.backend.service.OrganizationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(OrganizationController.class)
@AutoConfigureMockMvc(addFilters = false)
class OrganizationControllerTest {
    @Autowired MockMvc mvc;
    @MockitoBean OrganizationService service;

    @Test void rejectsInvalidCreateRequest() throws Exception {
        mvc.perform(post("/api/v1/organizations").contentType("application/json").content("{}"))
                .andExpect(status().isBadRequest()).andExpect(jsonPath("$.errors.name").exists());
        verifyNoInteractions(service);
    }

    @Test void createsOrganizationWithCreatedStatus() throws Exception {
        UUID id = UUID.randomUUID();
        when(service.create(any())).thenReturn(OrganizationResponse.builder().id(id).name("Hotel Group").build());
        mvc.perform(post("/api/v1/organizations").contentType("application/json").content("{\"name\":\"Hotel Group\"}"))
                .andExpect(status().isCreated()).andExpect(jsonPath("$.id").value(id.toString())).andExpect(jsonPath("$.name").value("Hotel Group"));
    }
}
