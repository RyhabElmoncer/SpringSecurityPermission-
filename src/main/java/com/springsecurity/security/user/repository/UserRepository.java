package com.springsecurity.security.user.repository;
import com.springsecurity.security.user.entity.Role;
import com.springsecurity.security.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByEmail(String email);

    @Query("SELECT u.id FROM User u WHERE u.email = :email")
    Optional<UUID> findUserIdByEmail(String email);

    boolean existsByEmail(String email);

    List<User> findAllByRole(Role role);
    long countByRole(Role role);

    Page<User> findByRole(Role role, Pageable pageable);
    Optional<User> findById(UUID id);

}
