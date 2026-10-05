package com.hrms.backend.controller;

import com.hrms.backend.dto.GuestHistoryResponse;
import com.hrms.backend.service.GuestHistoryService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/guests/{id}/history")
public class GuestHistoryController {
    private final GuestHistoryService service;
    public GuestHistoryController(GuestHistoryService service) { this.service = service; }
    @GetMapping public GuestHistoryResponse get(@PathVariable("id") UUID guestId) { return service.get(guestId); }
}
