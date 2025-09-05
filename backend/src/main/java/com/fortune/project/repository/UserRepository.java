package com.fortune.project.repository;

import com.fortune.project.entity.AppRole;
import com.fortune.project.entity.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {
    @EntityGraph(attributePaths = "roles")
    Optional<UserEntity> findByName(String username);
    boolean existsByEmail(String email);

    boolean existsByName(String user1);

    Page<UserEntity> findByRoles_RoleName(AppRole appRole, Pageable pageable);
}
