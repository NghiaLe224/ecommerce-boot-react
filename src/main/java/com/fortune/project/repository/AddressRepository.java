package com.fortune.project.repository;

import com.fortune.project.entity.AddressEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AddressRepository extends JpaRepository<AddressEntity, Long> {
    List<AddressEntity>findByUser_id(Long userId);

    Page<AddressEntity> findByIsDeletedFalse(Pageable pageable);

    Optional<AddressEntity> findByIdAndIsDeletedFalse(Long addressId);

    List<AddressEntity> findByUser_idAndIsDeletedFalse(Long userId);
}
