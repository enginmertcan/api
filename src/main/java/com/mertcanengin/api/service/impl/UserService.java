package com.mertcanengin.api.service.impl;

import com.mertcanengin.api.common.GeneralException;
import com.mertcanengin.api.entity.User;
import com.mertcanengin.api.entity.enums.Role;
import com.mertcanengin.api.repository.IUserRepository;
import com.mertcanengin.api.service.IUserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService implements IUserService {

    private final IUserRepository userRepository;

    public UserService(IUserRepository userRepository) {
        this.userRepository = userRepository;
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
if(!userRepository.existsById(id)){
    throw new GeneralException("User not found with id: " + id);
    }
        userRepository.deleteById(id);
    }
}
