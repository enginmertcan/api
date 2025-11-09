package com.mertcanengin.api.controller;

import com.mertcanengin.api.dto.AuthRequest;
import com.mertcanengin.api.dto.AuthResponse;
import com.mertcanengin.api.dto.RefreshTokenRequest;
import com.mertcanengin.api.dto.RegisterRequest;
import com.mertcanengin.api.security.JwtService;
import com.mertcanengin.api.mapper.UserMapper;
import com.mertcanengin.api.security.JwtService;
import com.mertcanengin.api.security.UserPrincipal;
import com.mertcanengin.api.service.IRefreshTokenService;
import com.mertcanengin.api.service.IUserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Auth", description = "Kimlik doğrulama uçları")
public class AuthController {

    private static final Logger log = LoggerFactory.getLogger(AuthController.class);

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final IRefreshTokenService refreshTokenService;
    private final IUserService userService;
    private final UserMapper userMapper;

    public AuthController(AuthenticationManager authenticationManager,
                          JwtService jwtService,
                          IRefreshTokenService refreshTokenService,
                          IUserService userService,
                          UserMapper userMapper) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.refreshTokenService = refreshTokenService;
        this.userService = userService;
        this.userMapper = userMapper;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody AuthRequest request) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.identityNo(), request.password())
            );
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            String accessToken = jwtService.generateToken(userDetails, Map.of("role", userDetails.getAuthorities()));
            String refreshToken = refreshTokenService.createToken(userDetails);
            return ResponseEntity.ok(new AuthResponse(accessToken, refreshToken));
        } catch (BadCredentialsException | UsernameNotFoundException ex) {
            log.warn("Login failed for identityNo {}: {}", request.identityNo(), ex.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Kimlik bilgileri doğrulanamadı"));
        }
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refresh(@Valid @RequestBody RefreshTokenRequest request) {
        UserDetails userDetails = refreshTokenService.validateAndGetUser(request.refreshToken());
        String accessToken = jwtService.generateToken(userDetails, Map.of("role", userDetails.getAuthorities()));
        String newRefreshToken = refreshTokenService.rotateToken(userDetails, request.refreshToken());
        return ResponseEntity.ok(new AuthResponse(accessToken, newRefreshToken));
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        UserDetails userDetails = new UserPrincipal(userService.save(userMapper.fromRegister(request)));
        String accessToken = jwtService.generateToken(userDetails, Map.of("role", userDetails.getAuthorities()));
        String refreshToken = refreshTokenService.createToken(userDetails);
        return ResponseEntity.ok(new AuthResponse(accessToken, refreshToken));
    }
}
