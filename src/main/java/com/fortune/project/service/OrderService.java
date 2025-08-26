package com.fortune.project.service;

import com.fortune.project.dto.request.order.CreateOrderRequest;
import com.fortune.project.dto.request.order.OrderResponse;
import com.fortune.project.dto.request.order.UpdateOrderStatusRequest;
import com.fortune.project.dto.response.common.ApiResponse;
import com.fortune.project.dto.response.common.PagingResponse;
import org.springframework.data.domain.Pageable;

public interface OrderService {
    OrderResponse createOrder(CreateOrderRequest request);

    PagingResponse<OrderResponse> getOrdersForCurrentUser(Pageable pageable);

    OrderResponse getOrderDetail(Long orderId);

    void cancelOrder(Long orderId);

    PagingResponse<OrderResponse> getAllOrders(Pageable pageable);

    ApiResponse<?> updateOrderStatus(UpdateOrderStatusRequest request);

    PagingResponse<OrderResponse> getOrdersForSeller(Pageable pageable, Long sellerId);
}
