package com.fortune.project.repository;

import com.fortune.project.entity.CartEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CartRepository extends JpaRepository<CartEntity, Long> {
//    CartEntity findByUser_email(String email);
    CartEntity findByIdAndUser_email(Long cartId, String email);




    Optional<CartEntity> findByUser_email(String email);
    Optional<CartEntity> findByUser_id(Long id);
}
