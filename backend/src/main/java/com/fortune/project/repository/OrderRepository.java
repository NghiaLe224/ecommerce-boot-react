package com.fortune.project.repository;

import com.fortune.project.entity.OrderEntity;
import com.fortune.project.entity.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<OrderEntity, Long> {
    Page<OrderEntity> findByCustomer(UserEntity user, Pageable pageable);

    @Query("select coalesce(sum(o.total), 0) from OrderEntity o")
    Double getTotalRevenue();

    @Query("""
        SELECT DISTINCT o
        FROM OrderEntity o
        JOIN o.items i
        WHERE i.product.user.id = :sellerId
    """)
    Page<OrderEntity> findOrdersBySellerId(@Param("sellerId") Long sellerId, Pageable pageable);
}
