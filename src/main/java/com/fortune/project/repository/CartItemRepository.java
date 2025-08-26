package com.fortune.project.repository;

import com.fortune.project.entity.CartItemEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CartItemRepository extends JpaRepository<CartItemEntity, Long> {
    CartItemEntity findByProduct_idAndCart_id(Long id, Long id1);

    List<CartItemEntity> findByCart_id(Long id);

    List<CartItemEntity> findByCart_Id(Long id);
}
