package com.fortune.project.service;

import com.fortune.project.dto.request.cart.CartItemDTO;
import com.fortune.project.dto.response.common.ApiResponse;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CartService {

    ApiResponse<?> addToCart(Long productId, Integer quantity, String email);

    ApiResponse<?> viewCart(Long id);

    ApiResponse<?> viewAllCart(Pageable pageable);

    void updateItemQuantity(Long id, Long productId, int quantity);

    void removeItem(Long id, Long productId);

    void syncCart(Long id, List<CartItemDTO> items);

    void removeAllItems(Long id);
}
