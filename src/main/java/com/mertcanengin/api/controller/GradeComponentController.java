package com.mertcanengin.api.controller;

import com.mertcanengin.api.dto.GradeComponentRequest;
import com.mertcanengin.api.dto.GradeComponentResponse;
import com.mertcanengin.api.mapper.GradeComponentMapper;
import com.mertcanengin.api.service.IGradeComponentService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/grade-components")
@Tag(name = "Grade Components", description = "Not bileşeni yönetimi")
public class GradeComponentController {

    private final IGradeComponentService gradeComponentService;
    private final GradeComponentMapper gradeComponentMapper;

    public GradeComponentController(IGradeComponentService gradeComponentService,
                                    GradeComponentMapper gradeComponentMapper) {
        this.gradeComponentService = gradeComponentService;
        this.gradeComponentMapper = gradeComponentMapper;
    }

    @PreAuthorize("hasAnyRole('ADMIN','TEACHER')")
    @GetMapping
    ResponseEntity<?> getComponents(@RequestParam(required = false) Integer lectureId,
                                    @RequestParam(defaultValue = "0") Integer page,
                                    @RequestParam(defaultValue = "10") Integer pageSize) {
        if (lectureId != null) {
            List<GradeComponentResponse> responses =
                    gradeComponentMapper.toResponseList(gradeComponentService.getByLecture(lectureId));
            return ResponseEntity.ok(responses);
        }
        Page<GradeComponentResponse> response = gradeComponentService
                .getAll(PageRequest.of(page, pageSize, Sort.by("id").descending()))
                .map(gradeComponentMapper::toResponse);
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasAnyRole('ADMIN','TEACHER')")
    @PostMapping
    ResponseEntity<GradeComponentResponse> create(@Valid @RequestBody GradeComponentRequest request) {
        return ResponseEntity.ok(
                gradeComponentMapper.toResponse(
                        gradeComponentService.save(gradeComponentMapper.toEntity(request))
                )
        );
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    ResponseEntity<Void> delete(@PathVariable Integer id) {
        gradeComponentService.delete(id);
        return ResponseEntity.ok().build();
    }
}
