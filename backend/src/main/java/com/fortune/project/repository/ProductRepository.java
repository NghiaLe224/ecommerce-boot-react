package com.fortune.project.repository;


import com.fortune.project.entity.ProductEntity;
import com.fortune.project.entity.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ProductRepository extends JpaRepository<ProductEntity, Long>, JpaSpecificationExecutor<ProductEntity> {
    Page<ProductEntity> findByCategory_id(Long categoryId, Pageable pageable);
    Page<ProductEntity> findByNameContainingIgnoreCase(String keyword, Pageable pageable);

    Page<ProductEntity> findAllByUser(UserEntity seller, Pageable pageable);
}
