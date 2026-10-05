package com.hrms.backend.controller;

import com.hrms.backend.dto.CheckoutPreviewResponse;
import com.hrms.backend.dto.CheckoutRequest;
import com.hrms.backend.dto.StayResponse;
import com.hrms.backend.service.StayService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/check-outs")
public class CheckoutController {
    private final StayService service;

    public CheckoutController(StayService service) {
        this.service = service;
    }

    @GetMapping("/{stayId}/preview")
    public CheckoutPreviewResponse preview(@PathVariable UUID stayId) {
        return service.checkoutPreview(stayId);
    }

    @PostMapping("/{stayId}")
    public StayResponse checkOut(@PathVariable UUID stayId,
                                 @Valid @RequestBody(required = false) CheckoutRequest request) {
        return service.checkOut(stayId, request);
    }
}
