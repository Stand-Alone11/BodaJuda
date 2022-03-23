package com.aeye.thirdeye.repository;

import com.aeye.thirdeye.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findOneByEmail(String email);

    User findByUserId(String userId);
}
