package com.mertcanengin.api.service;

import com.mertcanengin.api.entity.User;
import com.mertcanengin.api.entity.enums.Role;

import java.util.List;

public interface IUserService extends IService<User>{

    List<User> getUsersByRole(Role role);
    List<User> getPotentialUsers(List<Integer> ids);
    User register(User user);
    User updateMfaPreference(Integer userId, boolean enabled);
}
