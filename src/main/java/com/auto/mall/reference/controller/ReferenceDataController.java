package com.auto.mall.reference.controller;

import com.auto.mall.reference.dto.ReferenceDataResponse;
import com.auto.mall.reference.service.ReferenceDataService;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reference")
@RequiredArgsConstructor
public class ReferenceDataController {

    private final ReferenceDataService service;

    @GetMapping("/full")
    public ReferenceDataResponse getFull() {
        return service.getFull();
    }
}