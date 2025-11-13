package com.mertcanengin.api.repository;

import com.mertcanengin.api.entity.User;
import com.mertcanengin.api.entity.enums.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IUserRepository extends JpaRepository<User,Integer> {
    boolean existsByIdentityNo(String identityNo);
    boolean existsByEmail(String email);
    boolean existsByEmailAndIdNot(String email, Integer id);
    List<User> findAllByRole(Role role);
    List <User> findAllByRoleAndIdIsNotIn(Role role, List<Integer> idList);
    Optional<User> findByIdentityNo(String identityNo);
    Optional<User> findByEmail(String email);
    long countByRole(Role role);
}
