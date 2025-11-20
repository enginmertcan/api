package com.mertcanengin.api.service.impl;

import com.mertcanengin.api.common.GeneralException;
import com.mertcanengin.api.domain.user.policy.UserCreationPolicy;
import com.mertcanengin.api.domain.user.policy.UserDeletionGuard;
import com.mertcanengin.api.domain.user.policy.UserUpdatePolicy;
import com.mertcanengin.api.entity.User;
import com.mertcanengin.api.entity.enums.Role;
import com.mertcanengin.api.repository.IUserRepository;
import com.mertcanengin.api.service.IRefreshTokenService;
import com.mertcanengin.api.service.IUserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService implements IUserService {

    private final IUserRepository userRepository;
    private final IRefreshTokenService refreshTokenService;
    private final UserCreationPolicy userCreationPolicy;
    private final UserUpdatePolicy userUpdatePolicy;
    private final UserDeletionGuard userDeletionGuard;

    public UserService(IUserRepository userRepository,
                       IRefreshTokenService refreshTokenService,
                       UserCreationPolicy userCreationPolicy,
                       UserUpdatePolicy userUpdatePolicy,
                       UserDeletionGuard userDeletionGuard) {
        this.userRepository = userRepository;
        this.refreshTokenService = refreshTokenService;
        this.userCreationPolicy = userCreationPolicy;
        this.userUpdatePolicy = userUpdatePolicy;
        this.userDeletionGuard = userDeletionGuard;
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public List<User> getUsersByRole(Role role) {
        return userRepository.findAllByRole(role);
    }

    @Override
    @PreAuthorize("hasAnyRole('ADMIN','TEACHER')")
    public List<User> getPotentialUsers(List<Integer> ids) {
        if (ids.isEmpty()) {
            return userRepository.findAllByRole(Role.STUDENT);
        }
        return userRepository.findAllByRoleAndIdIsNotIn(Role.STUDENT, ids);
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public User save(User user) {
        if (user == null) {
            throw new GeneralException("User payload cannot be empty.");
        }
        if (user.getId() == null) {
            userCreationPolicy.apply(user);
        } else {
            User existing = loadUserInternal(user.getId());
            userUpdatePolicy.apply(user, existing);
        }
        return userRepository.save(user);
    }

    @Override
    @PreAuthorize("hasAnyRole('ADMIN','TEACHER')")
    public User getById(Integer id) {
        return loadUserInternal(id);
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public List<User> getAll() {
        return userRepository.findAll();
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public Page<User> getAll(Pageable pageable) {
        return userRepository.findAll(pageable);
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public void delete(Integer id) {
        User user = loadUserInternal(id);
        userDeletionGuard.ensureCanDelete(user);
        refreshTokenService.revokeUserTokens(id);
        userRepository.deleteById(id);
    }

    @Override
    @PreAuthorize("permitAll()")
    public User register(User user) {
        if (user == null) {
            throw new GeneralException("User payload cannot be empty.");
        }
        userCreationPolicy.apply(user);
        return userRepository.save(user);
    }

    @Override
    @PreAuthorize("hasRole('ADMIN') or #userId == principal.user.id")
    public User updateMfaPreference(Integer userId, boolean enabled) {
        User user = loadUserInternal(userId);
        user.setMfaEnabled(enabled);
        return userRepository.save(user);
    }

    private User loadUserInternal(Integer id) {
        Optional<User> user = userRepository.findById(id);
        if (user.isEmpty()) {
            throw new GeneralException("User not found with id: " + id);
        }
        return user.get();
    }
}
