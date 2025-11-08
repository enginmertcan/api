package com.mertcanengin.api.controller;

import com.mertcanengin.api.dto.UserRequest;
import com.mertcanengin.api.dto.UserResponse;
import com.mertcanengin.api.entity.enums.Role;
import com.mertcanengin.api.mapper.UserMapper;
import com.mertcanengin.api.service.IUserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@Tag(name = "Users", description = "Kullanıcı yönetimi uçları")
public class UserController {
    private final IUserService userService;
    private final UserMapper userMapper;

    public UserController(IUserService userService, UserMapper userMapper) {
        this.userService = userService;
        this.userMapper = userMapper;
    }


    @GetMapping
    ResponseEntity<Page<UserResponse>> getUsers(@RequestParam(defaultValue = "0") Integer page,
                                        @RequestParam(defaultValue = "10") Integer pageSize){
        return ResponseEntity.ok(
                userService.getAll(PageRequest.of(page,pageSize, Sort.by("id")))
                        .map(userMapper::toResponse)
        );
    }

    @GetMapping("/by-role")
    ResponseEntity<List<UserResponse>> getUsersByRole(@RequestParam Role role){
        return ResponseEntity.ok(userMapper.toResponseList(userService.getUsersByRole(role)));
    }

    @GetMapping("/{id}")
    ResponseEntity<UserResponse> getUser(@PathVariable Integer id){
        return ResponseEntity.ok(userMapper.toResponse(userService.getById(id)));
    }

    @PostMapping
    ResponseEntity<UserResponse> createUser(@Valid @RequestBody UserRequest request){
        return ResponseEntity.ok(userMapper.toResponse(userService.save(userMapper.toEntity(request))));
    }

    @DeleteMapping
    ResponseEntity<Void> deleteUser(@RequestParam Integer id){
        userService.delete(id);
        return ResponseEntity.ok().build();
    }
}
