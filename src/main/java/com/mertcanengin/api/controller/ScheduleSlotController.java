package com.mertcanengin.api.controller;

import com.mertcanengin.api.dto.ScheduleSlotRequest;
import com.mertcanengin.api.dto.ScheduleSlotResponse;
import com.mertcanengin.api.mapper.ScheduleSlotMapper;
import com.mertcanengin.api.service.IScheduleSlotService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/schedule-slots")
public class ScheduleSlotController {

    private final IScheduleSlotService scheduleSlotService;
    private final ScheduleSlotMapper scheduleSlotMapper;

    public ScheduleSlotController(IScheduleSlotService scheduleSlotService, ScheduleSlotMapper scheduleSlotMapper) {
        this.scheduleSlotService = scheduleSlotService;
        this.scheduleSlotMapper = scheduleSlotMapper;
    }

    @PreAuthorize("hasAnyRole('ADMIN','TEACHER','STUDENT')")
    @GetMapping
    ResponseEntity<Page<ScheduleSlotResponse>> getSlots(@RequestParam(defaultValue = "0") Integer page,
                                                        @RequestParam(defaultValue = "10") Integer pageSize) {
        Sort sort = Sort.by("dayOfWeek").and(Sort.by("startTime"));
        return ResponseEntity.ok(
                scheduleSlotService.getAll(PageRequest.of(page, pageSize, sort))
                        .map(scheduleSlotMapper::toResponse)
        );
    }

    @PreAuthorize("hasAnyRole('ADMIN','TEACHER','STUDENT')")
    @GetMapping("/{id}")
    ResponseEntity<ScheduleSlotResponse> getSlot(@PathVariable Integer id) {
        return ResponseEntity.ok(scheduleSlotMapper.toResponse(scheduleSlotService.getById(id)));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    ResponseEntity<ScheduleSlotResponse> createSlot(@Valid @RequestBody ScheduleSlotRequest request) {
        return ResponseEntity.ok(
                scheduleSlotMapper.toResponse(scheduleSlotService.save(scheduleSlotMapper.toEntity(request)))
        );
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    ResponseEntity<Void> deleteSlot(@PathVariable Integer id) {
        scheduleSlotService.delete(id);
        return ResponseEntity.ok().build();
    }
}
