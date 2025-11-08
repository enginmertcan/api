package com.mertcanengin.api.controller;

import com.mertcanengin.api.dto.AuthRequest;
import com.mertcanengin.api.dto.AuthResponse;
import com.mertcanengin.api.dto.RefreshTokenRequest;
import com.mertcanengin.api.security.JwtService;
import com.mertcanengin.api.service.IRefreshTokenService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Auth", description = "Kimlik doğrulama uçları")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final IRefreshTokenService refreshTokenService;

    public AuthController(AuthenticationManager authenticationManager,
                          JwtService jwtService,
                          IRefreshTokenService refreshTokenService) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.refreshTokenService = refreshTokenService;
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody AuthRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.identityNo(), request.password())
        );
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String accessToken = jwtService.generateToken(userDetails, Map.of("role", userDetails.getAuthorities()));
        String refreshToken = refreshTokenService.createToken(userDetails);
        return ResponseEntity.ok(new AuthResponse(accessToken, refreshToken));
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refresh(@Valid @RequestBody RefreshTokenRequest request) {
        UserDetails userDetails = refreshTokenService.validateAndGetUser(request.refreshToken());
        String accessToken = jwtService.generateToken(userDetails, Map.of("role", userDetails.getAuthorities()));
        String newRefreshToken = refreshTokenService.rotateToken(userDetails, request.refreshToken());
        return ResponseEntity.ok(new AuthResponse(accessToken, newRefreshToken));
    }
}
