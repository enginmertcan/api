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
    public List<User> getUsersByRole(Role role) {
        return userRepository.findAllByRole(role);
    }

    @Override
    public List<User> getPotentialUsers(List<Integer> ids) {
        if (ids.isEmpty()) {
            return getUsersByRole(Role.STUDENT);
        }
        return userRepository.findAllByRoleAndIdIsNotIn(Role.STUDENT, ids);
    }

    @Override
    public User save(User user) {
        if (user == null) {
            throw new GeneralException("User payload cannot be empty.");
        }
        if (user.getId() == null) {
            userCreationPolicy.apply(user);
        } else {
            User existing = getById(user.getId());
            userUpdatePolicy.apply(user, existing);
        }
        return userRepository.save(user);
    }

    @Override
    public User getById(Integer id) {
        Optional<User> user = userRepository.findById(id);
        if(user.isEmpty()){
            throw new GeneralException("User not found with id: " + id);
        }
        return user.get();
    }

    @Override
    public List<User> getAll() {
        return userRepository.findAll();
    }

    @Override
    public Page<User> getAll(Pageable pageable) {
        return userRepository.findAll(pageable);
    }

    @Override
    public void delete(Integer id) {
        User user = getById(id);
        userDeletionGuard.ensureCanDelete(user);
        refreshTokenService.revokeUserTokens(id);
        userRepository.deleteById(id);
    }
}
