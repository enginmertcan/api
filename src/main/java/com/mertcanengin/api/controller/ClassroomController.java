package com.mertcanengin.api.controller;

import com.mertcanengin.api.dto.ClassroomRequest;
import com.mertcanengin.api.dto.ClassroomResponse;
import com.mertcanengin.api.mapper.ClassroomMapper;
import com.mertcanengin.api.service.IClassroomService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/classrooms")
public class ClassroomController {

    private final IClassroomService classroomService;
    private final ClassroomMapper classroomMapper;

    public ClassroomController(IClassroomService classroomService, ClassroomMapper classroomMapper) {
        this.classroomService = classroomService;
        this.classroomMapper = classroomMapper;
    }

    @GetMapping
    ResponseEntity<Page<ClassroomResponse>> getClassrooms(@RequestParam(defaultValue = "0") Integer page,
                                                         @RequestParam(defaultValue = "10") Integer pageSize) {
        return ResponseEntity.ok(
                classroomService.getAll(PageRequest.of(page, pageSize, Sort.by("name").ascending()))
                        .map(classroomMapper::toResponse)
        );
    }

    @GetMapping("/{id}")
    ResponseEntity<ClassroomResponse> getClassroom(@PathVariable Integer id) {
        return ResponseEntity.ok(classroomMapper.toResponse(classroomService.getById(id)));
    }

    @PostMapping
    ResponseEntity<ClassroomResponse> createClassroom(@Valid @RequestBody ClassroomRequest request) {
        return ResponseEntity.ok(
                classroomMapper.toResponse(classroomService.save(classroomMapper.toEntity(request)))
        );
    }

    @DeleteMapping("/{id}")
    ResponseEntity<Void> deleteClassroom(@PathVariable Integer id) {
        classroomService.delete(id);
        return ResponseEntity.ok().build();
    }
}
