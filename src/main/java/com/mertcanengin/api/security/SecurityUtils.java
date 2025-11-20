package com.mertcanengin.api.security;

import java.util.Arrays;
import java.util.Optional;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import com.mertcanengin.api.entity.enums.Role;

public final class SecurityUtils {

    private SecurityUtils() {
    }

    public static Optional<UserPrincipal> getCurrentUserPrincipal() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return Optional.empty();
        }
        Object principal = authentication.getPrincipal();
        if (principal instanceof UserPrincipal userPrincipal) {
            return Optional.of(userPrincipal);
        }
        if (principal instanceof UserDetails userDetails && userDetails instanceof UserPrincipal userPrincipal) {
            return Optional.of(userPrincipal);
        }
        return Optional.empty();
    }

    public static Optional<Integer> getCurrentUserId() {
        return getCurrentUserPrincipal().map(p -> p.getUser().getId());
    }

    public static boolean hasRole(Role role) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getAuthorities() == null) {
            return false;
        }
        String target = "ROLE_" + role.name();
        return authentication.getAuthorities().stream()
                .anyMatch(authority -> target.equals(authority.getAuthority()));
    }

    public static boolean hasAnyRole(Role... roles) {
        return Arrays.stream(roles).anyMatch(SecurityUtils::hasRole);
    }
}
