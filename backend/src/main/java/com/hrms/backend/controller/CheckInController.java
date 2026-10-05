package com.hrms.backend.controller;

import com.hrms.backend.dto.CheckInRequest;
import com.hrms.backend.dto.StayResponse;
import com.hrms.backend.service.StayService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/check-ins")
public class CheckInController {
    private final StayService service;

    public CheckInController(StayService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public StayResponse checkIn(@Valid @RequestBody CheckInRequest request) {
        return service.checkIn(request);
    }
}
