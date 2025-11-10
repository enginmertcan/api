package com.mertcanengin.api.service.impl;

import com.mertcanengin.api.common.GeneralException;
import com.mertcanengin.api.entity.User;
import com.mertcanengin.api.entity.enums.Role;
import com.mertcanengin.api.repository.IEnrollmentRepository;
import com.mertcanengin.api.repository.ILectureRepository;
import com.mertcanengin.api.repository.IUserRepository;
import com.mertcanengin.api.service.IRefreshTokenService;
import com.mertcanengin.api.service.IUserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

@Service
public class UserService implements IUserService {

    private static final Pattern STRONG_PASSWORD_PATTERN =
            Pattern.compile("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[^A-Za-z0-9]).{8,}$");
    private static final String PASSWORD_POLICY_MESSAGE =
            "Password must be at least 8 characters and include upper, lower, numeric and special characters.";

    private final IUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final IRefreshTokenService refreshTokenService;
    private final ILectureRepository lectureRepository;
    private final IEnrollmentRepository enrollmentRepository;

    public UserService(IUserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       IRefreshTokenService refreshTokenService,
                       ILectureRepository lectureRepository,
                       IEnrollmentRepository enrollmentRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.refreshTokenService = refreshTokenService;
        this.lectureRepository = lectureRepository;
        this.enrollmentRepository = enrollmentRepository;
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
        if (user.getId() == null) {
           if (user.getIdentityNo() == null || user.getIdentityNo().length() != 11) {
                throw new GeneralException("Identity number must be exactly 11 characters long.");
            }
            if(userRepository.existsByIdentityNo(user.getIdentityNo())) {
                throw new GeneralException("A user with this identity number already exists.");
            }
            encodePassword(user);
        } else {
            User existing = getById(user.getId());
            if (user.getPassword() == null || user.getPassword().isBlank()) {
                user.setPassword(existing.getPassword());
            } else if (!user.getPassword().equals(existing.getPassword())) {
                encodePassword(user);
            }
        }
        return userRepository.save(user);
    }

    private void encodePassword(User user) {
        String rawPassword = user.getPassword();
        if (rawPassword == null || rawPassword.isBlank() || !STRONG_PASSWORD_PATTERN.matcher(rawPassword).matches()) {
            throw new GeneralException(PASSWORD_POLICY_MESSAGE);
        }
        user.setPassword(passwordEncoder.encode(rawPassword));
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
        ensureUserHasNoDependencies(user);
        refreshTokenService.revokeUserTokens(id);
        userRepository.deleteById(id);
    }

    private void ensureUserHasNoDependencies(User user) {
        boolean hasLectures = lectureRepository.existsByTeacher_Id(user.getId());
        boolean hasEnrollments = enrollmentRepository.existsByStudent_Id(user.getId());

        if (hasLectures || hasEnrollments) {
            List<String> reasons = new ArrayList<>();
            if (hasLectures) {
                reasons.add("they are assigned as a teacher to existing lectures");
            }
            if (hasEnrollments) {
                reasons.add("they are enrolled in existing lectures");
            }
            throw new GeneralException("User cannot be deleted because " +
                    String.join(" and ", reasons) +
                    ". Remove the related records first.");
        }
    }
}
