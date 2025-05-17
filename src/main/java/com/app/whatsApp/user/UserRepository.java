package com.app.whatsApp.user;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<User> findUserByPhone(String phone);

    User findUserByRestToken(String token);
}
