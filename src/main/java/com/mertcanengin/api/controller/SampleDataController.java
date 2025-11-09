package com.mertcanengin.api.controller;

import com.mertcanengin.api.bootstrap.SampleDataService;
import com.mertcanengin.api.dto.bootstrap.SampleDataStatusResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/bootstrap")
@Tag(name = "Sample Data", description = "Demo verisi oluşturma uçları")
public class SampleDataController {

    private final SampleDataService sampleDataService;

    public SampleDataController(SampleDataService sampleDataService) {
        this.sampleDataService = sampleDataService;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/seed")
    public ResponseEntity<SampleDataStatusResponse> seed(@RequestParam(defaultValue = "false") boolean force) {
        return ResponseEntity.ok(sampleDataService.bootstrap(force));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/status")
    public ResponseEntity<SampleDataStatusResponse> status() {
        return ResponseEntity.ok(sampleDataService.status());
    }
}
