package com.restaurant.erp.user.repository;

import com.restaurant.erp.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);

    @Query("SELECT u FROM User u WHERE LOWER(u.username) = LOWER(:identifier) " +
           "OR LOWER(u.username) = LOWER(CONCAT(:identifier, '@restaurant.com')) " +
           "OR LOWER(u.username) LIKE LOWER(CONCAT(:identifier, '@%'))")
    Optional<User> findByUsernameOrEmail(@Param("identifier") String identifier);
}
