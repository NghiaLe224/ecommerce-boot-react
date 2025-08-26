package com.fortune.project.dto.request.order;

import com.fortune.project.dto.request.address.AddressResponse;
import com.fortune.project.entity.OrderEntity;
import com.fortune.project.entity.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderResponse {

    private Long id;
    private OrderStatus status;
    private Double subtotal;
    private Double shippingFee;
    private Double total;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private List<OrderItemResponse> items;
    private AddressResponse shippingAddress;
    private List<PaymentResponse> payment;

    public OrderResponse(Long id, OrderStatus status, Double subtotal, Double shippingFee, Double total, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.status = status;
        this.subtotal = subtotal;
        this.shippingFee = shippingFee;
        this.total = total;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static OrderResponse from(OrderEntity order){
        return new OrderResponse(
                order.getId(),
                order.getStatus(),
                order.getSubtotal(),
                order.getShippingFee(),
                order.getTotal(),
                order.getCreatedAt(),
                order.getUpdatedAt()
        );
    }

    public static OrderResponse from(OrderEntity order, List<PaymentResponse> paymentResponses) {
        return new OrderResponse(
                order.getId(),
                order.getStatus(),
                order.getSubtotal(),
                order.getShippingFee(),
                order.getTotal(),
                order.getCreatedAt(),
                order.getUpdatedAt(),
                order.getItems().stream()
                        .map(OrderItemResponse::from)
                        .toList(),
                AddressResponse.from(order.getAddress()),
                paymentResponses
        );
    }
}

