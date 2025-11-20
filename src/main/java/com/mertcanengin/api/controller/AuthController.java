package com.mertcanengin.api.controller;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mertcanengin.api.dto.ActiveSessionResponse;
import com.mertcanengin.api.dto.AuthRequest;
import com.mertcanengin.api.dto.AuthResponse;
import com.mertcanengin.api.dto.EmailVerificationRequest;
import com.mertcanengin.api.dto.ForgotPasswordRequest;
import com.mertcanengin.api.dto.MfaChallengeResponse;
import com.mertcanengin.api.dto.PasswordResetRequest;
import com.mertcanengin.api.dto.RefreshTokenRequest;
import com.mertcanengin.api.dto.RegisterRequest;
import com.mertcanengin.api.dto.RegistrationPendingResponse;
import com.mertcanengin.api.dto.ResendVerificationRequest;
import com.mertcanengin.api.entity.User;
import com.mertcanengin.api.mapper.UserMapper;
import com.mertcanengin.api.security.JwtService;
import com.mertcanengin.api.security.UserPrincipal;
import com.mertcanengin.api.security.mfa.MfaChallenge;
import com.mertcanengin.api.security.mfa.MfaService;
import com.mertcanengin.api.security.password.PasswordResetService;
import com.mertcanengin.api.security.session.DeviceMetadata;
import com.mertcanengin.api.security.session.RefreshSession;
import com.mertcanengin.api.service.EmailVerificationService;
import com.mertcanengin.api.service.IRefreshTokenService;
import com.mertcanengin.api.service.IUserService;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Auth", description = "Kimlik doğrulama uçları")
public class AuthController {

    private static final Logger log = LoggerFactory.getLogger(AuthController.class);

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final IRefreshTokenService refreshTokenService;
    private final IUserService userService;
    private final EmailVerificationService emailVerificationService;
    private final MfaService mfaService;
    private final PasswordResetService passwordResetService;
    private final UserMapper userMapper;

    public AuthController(AuthenticationManager authenticationManager,
                          JwtService jwtService,
                          IRefreshTokenService refreshTokenService,
                          IUserService userService,
                          EmailVerificationService emailVerificationService,
                          MfaService mfaService,
                          PasswordResetService passwordResetService,
                          UserMapper userMapper) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.refreshTokenService = refreshTokenService;
        this.userService = userService;
        this.emailVerificationService = emailVerificationService;
        this.mfaService = mfaService;
        this.passwordResetService = passwordResetService;
        this.userMapper = userMapper;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody AuthRequest request,
                                   HttpServletRequest httpRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.identityNo(), request.password())
            );
            UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();
            DeviceMetadata metadata = buildDeviceMetadata(request.deviceId(), request.deviceName(), httpRequest);

            if (principal.getUser().isMfaEnabled()) {
                if (request.challengeId() == null || request.mfaCode() == null) {
                    MfaChallenge challenge = mfaService.initiate(principal.getUser());
                    return ResponseEntity.status(HttpStatus.ACCEPTED)
                            .body(new MfaChallengeResponse(
                                    challenge.challengeId(),
                                    challenge.expiresAt(),
                                    challenge.channel(),
                                    "MFA doğrulaması gerekli. Kod e-posta adresine gönderildi."
                            ));
                }
                mfaService.verify(request.challengeId(), request.mfaCode(), principal.getUser());
            }

            return ResponseEntity.ok(issueAuthTokens(principal, metadata));
        } catch (DisabledException ex) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("message", "Hesabın henüz doğrulanmadı. Lütfen e-posta doğrulamasını tamamla."));
        } catch (BadCredentialsException | UsernameNotFoundException ex) {
            log.warn("Login failed for identityNo {}: {}", request.identityNo(), ex.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Kimlik bilgileri doğrulanamadı"));
        }
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refresh(@Valid @RequestBody RefreshTokenRequest request,
                                                HttpServletRequest httpRequest) {
        UserDetails userDetails = refreshTokenService.validateAndGetUser(request.refreshToken());
        RefreshSession session = refreshTokenService.getSession(request.refreshToken());
        DeviceMetadata metadata = buildDeviceMetadata(session.deviceId(), session.deviceName(), httpRequest);
        String accessToken = jwtService.generateToken(userDetails, Map.of("role", userDetails.getAuthorities()));
        String newRefreshToken = refreshTokenService.rotateToken(userDetails, request.refreshToken(), metadata);
        return ResponseEntity.ok(new AuthResponse(accessToken, newRefreshToken, metadata.deviceId()));
    }

    @PostMapping("/register")
    public ResponseEntity<RegistrationPendingResponse> register(@Valid @RequestBody RegisterRequest request) {
        User savedUser = userService.register(userMapper.fromRegister(request));
        emailVerificationService.startVerification(savedUser);
        return ResponseEntity.ok(
                new RegistrationPendingResponse(
                        "E-posta adresine doğrulama kodu gönderildi. Lütfen kodu girerek hesabını aktifleştir.",
                        savedUser.getIdentityNo(),
                        savedUser.getEmail()
                )
        );
    }

    @PostMapping("/verify-email")
    public ResponseEntity<AuthResponse> verifyEmail(@Valid @RequestBody EmailVerificationRequest request,
                                                    HttpServletRequest httpRequest) {
        User verifiedUser = emailVerificationService.verifyCode(request.identityNo(), request.code());
        UserDetails userDetails = new UserPrincipal(verifiedUser);
        DeviceMetadata metadata = buildDeviceMetadata(null, "Verified Device", httpRequest);
        return ResponseEntity.ok(issueAuthTokens(userDetails, metadata));
    }

    @PostMapping("/resend-verification")
    public ResponseEntity<Map<String, String>> resendVerification(@Valid @RequestBody ResendVerificationRequest request) {
        emailVerificationService.resendCode(request.identityNo());
        return ResponseEntity.ok(Map.of("message", "Doğrulama kodu yeniden gönderildi"));
    }

    @PostMapping("/password/forgot")
    public ResponseEntity<Map<String, String>> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
        passwordResetService.requestReset(request.identityNo(), request.email());
        return ResponseEntity.ok(Map.of("message", "Parola sıfırlama kodu e-posta adresine gönderildi."));
    }

    @PostMapping("/password/reset")
    public ResponseEntity<Map<String, String>> resetPassword(@Valid @RequestBody PasswordResetRequest request) {
        passwordResetService.resetPassword(request.token(), request.newPassword());
        return ResponseEntity.ok(Map.of("message", "Parolan başarıyla güncellendi."));
    }

    @GetMapping("/sessions")
    public ResponseEntity<List<ActiveSessionResponse>> activeSessions(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestHeader(value = "X-Device-Id", required = false) String currentDeviceId) {
        List<RefreshSession> sessions = refreshTokenService.getActiveSessions(principal.getUser().getId());
        List<ActiveSessionResponse> response = sessions.stream()
                .map(session -> new ActiveSessionResponse(
                        session.deviceId(),
                        session.deviceName(),
                        session.ipAddress(),
                        session.userAgent(),
                        session.issuedAt(),
                        session.expiresAt(),
                        Objects.equals(session.deviceId(), currentDeviceId)
                ))
                .toList();
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/sessions/{deviceId}")
    public ResponseEntity<Void> revokeSession(@AuthenticationPrincipal UserPrincipal principal,
                                              @PathVariable String deviceId) {
        refreshTokenService.revokeSession(principal.getUser().getId(), deviceId);
        return ResponseEntity.noContent().build();
    }

    private AuthResponse issueAuthTokens(UserDetails userDetails, DeviceMetadata metadata) {
        String accessToken = jwtService.generateToken(userDetails, Map.of("role", userDetails.getAuthorities()));
        String refreshToken = refreshTokenService.createToken(userDetails, metadata);
        return new AuthResponse(accessToken, refreshToken, metadata.deviceId());
    }

    private DeviceMetadata buildDeviceMetadata(String deviceId, String deviceName, HttpServletRequest request) {
        return DeviceMetadata.of(
                deviceId,
                deviceName != null ? deviceName : defaultDeviceName(request.getHeader("User-Agent")),
                resolveClientIp(request),
                request.getHeader("User-Agent")
        );
    }

    private String resolveClientIp(HttpServletRequest request) {
        String forwarded = request.getHeader("X-Forwarded-For");
        if (forwarded != null && !forwarded.isBlank()) {
            return forwarded.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }

    private String defaultDeviceName(String userAgent) {
        if (userAgent == null || userAgent.isBlank()) {
            return "Unknown Device";
        }
        if (userAgent.contains("Windows")) {
            return "Windows Client";
        }
        if (userAgent.contains("Mac")) {
            return "macOS Client";
        }
        if (userAgent.contains("Linux")) {
            return "Linux Client";
        }
        return "Web Client";
    }
}
