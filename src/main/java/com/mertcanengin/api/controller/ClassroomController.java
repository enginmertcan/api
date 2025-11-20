package com.mertcanengin.api.controller;

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

import com.mertcanengin.api.dto.ClassroomRequest;
import com.mertcanengin.api.dto.ClassroomResponse;
import com.mertcanengin.api.mapper.ClassroomMapper;
import com.mertcanengin.api.service.IClassroomService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/classrooms")
public class ClassroomController {

    private final IClassroomService classroomService;
    private final ClassroomMapper classroomMapper;

    public ClassroomController(IClassroomService classroomService, ClassroomMapper classroomMapper) {
        this.classroomService = classroomService;
        this.classroomMapper = classroomMapper;
    }

    @PreAuthorize("hasAnyRole('ADMIN','TEACHER','STUDENT')")
    @GetMapping
    ResponseEntity<Page<ClassroomResponse>> getClassrooms(@RequestParam(defaultValue = "0") Integer page,
                                                         @RequestParam(defaultValue = "10") Integer pageSize) {
        return ResponseEntity.ok(
                classroomService.getAll(PageRequest.of(page, pageSize, Sort.by("name").ascending()))
                        .map(classroomMapper::toResponse)
        );
    }

    @PreAuthorize("hasAnyRole('ADMIN','TEACHER','STUDENT')")
    @GetMapping("/{id}")
    ResponseEntity<ClassroomResponse> getClassroom(@PathVariable Integer id) {
        return ResponseEntity.ok(classroomMapper.toResponse(classroomService.getById(id)));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    ResponseEntity<ClassroomResponse> createClassroom(@Valid @RequestBody ClassroomRequest request) {
        return ResponseEntity.ok(
                classroomMapper.toResponse(classroomService.save(classroomMapper.toEntity(request)))
        );
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    ResponseEntity<Void> deleteClassroom(@PathVariable Integer id) {
        classroomService.delete(id);
        return ResponseEntity.ok().build();
    }
}
