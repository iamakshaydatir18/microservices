package com.microservices.userservice.repository;

import com.microservices.userservice.enums.Active;
import com.microservices.userservice.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, String> {

    Optional<User> findByEmail(String email);

    Optional<User> findByUsername(String username);

    List<User> findAllByActive(Active active);

    @Query("SELECT u FROM users u WHERE u.id = :id")
    Optional<User> findExplicitById(@Param("id") String id);
}
