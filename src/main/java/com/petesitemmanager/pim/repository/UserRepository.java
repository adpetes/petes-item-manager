package com.petesitemmanager.pim.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.petesitemmanager.pim.domain.User;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByBungieId(String bungieId);

    Optional<User> findBySessionToken(String sessionToken);
}
