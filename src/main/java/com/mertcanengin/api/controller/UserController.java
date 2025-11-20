package com.mertcanengin.api.controller;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import com.mertcanengin.api.dto.MfaPreferenceRequest;
import com.mertcanengin.api.dto.UserRequest;
import com.mertcanengin.api.dto.UserResponse;
import com.mertcanengin.api.entity.enums.Role;
import com.mertcanengin.api.mapper.UserMapper;
import com.mertcanengin.api.security.UserPrincipal;
import com.mertcanengin.api.service.IUserService;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

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
    public ResponseEntity<Page<UserResponse>> getUsers(@RequestParam(defaultValue = "0") Integer page,
                                                       @RequestParam(defaultValue = "10") Integer pageSize) {
        return ResponseEntity.ok(
                userService.getAll(PageRequest.of(page, pageSize, Sort.by("id")))
                        .map(userMapper::toResponse)
        );
    }

    @GetMapping("/by-role")
    public ResponseEntity<List<UserResponse>> getUsersByRole(@RequestParam Role role) {
        return ResponseEntity.ok(userMapper.toResponseList(userService.getUsersByRole(role)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUser(@PathVariable Integer id) {
        return ResponseEntity.ok(userMapper.toResponse(userService.getById(id)));
    }

    @GetMapping("/me")
    public ResponseEntity<UserResponse> getCurrentUser(@AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(userMapper.toResponse(principal.getUser()));
    }

    @PatchMapping("/me/mfa")
    public ResponseEntity<UserResponse> updateMfaPreference(@AuthenticationPrincipal UserPrincipal principal,
                                                            @Valid @RequestBody MfaPreferenceRequest request) {
        return ResponseEntity.ok(
                userMapper.toResponse(userService.updateMfaPreference(principal.getUser().getId(), request.enabled()))
        );
    }

    @PostMapping
    public ResponseEntity<UserResponse> createUser(@Valid @RequestBody UserRequest request) {
        return ResponseEntity.ok(userMapper.toResponse(userService.save(userMapper.toEntity(request))));
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteUser(@RequestParam Integer id) {
        userService.delete(id);
        return ResponseEntity.ok().build();
    }
}
