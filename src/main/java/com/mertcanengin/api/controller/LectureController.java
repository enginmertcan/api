package com.mertcanengin.api.controller;

import com.mertcanengin.api.dto.LectureRequest;
import com.mertcanengin.api.dto.LectureResponse;
import com.mertcanengin.api.mapper.LectureMapper;
import com.mertcanengin.api.service.ILectureService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/lectures")
@Tag(name = "Lectures", description = "Ders yönetimi uçları")
public class LectureController {

    private final ILectureService lectureService;
    private final LectureMapper lectureMapper;

    public LectureController(ILectureService lectureService, LectureMapper lectureMapper) {
        this.lectureService = lectureService;
        this.lectureMapper = lectureMapper;
    }

    @PreAuthorize("hasAnyRole('ADMIN','TEACHER','STUDENT')")
    @GetMapping
    ResponseEntity<Page<LectureResponse>> getLectures(@RequestParam(defaultValue = "0") Integer page,
                                                      @RequestParam(defaultValue = "10") Integer pageSize) {
        return ResponseEntity.ok(
                lectureService.getAll(PageRequest.of(page, pageSize, Sort.by("id").ascending()))
                        .map(lectureMapper::toResponse)
        );
    }

    @PreAuthorize("hasAnyRole('ADMIN','TEACHER','STUDENT')")
    @GetMapping("/{id}")
    ResponseEntity<LectureResponse> getLecture(@PathVariable Integer id) {
        return ResponseEntity.ok(lectureMapper.toResponse(lectureService.getById(id)));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    ResponseEntity<LectureResponse> createLecture(@Valid @RequestBody LectureRequest request) {
        return ResponseEntity.ok(
                lectureMapper.toResponse(
                        lectureService.save(lectureMapper.toEntity(request))
                )
        );
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    ResponseEntity<Void> deleteLecture(@PathVariable Integer id) {
        lectureService.delete(id);
        return ResponseEntity.ok().build();
    }
}
