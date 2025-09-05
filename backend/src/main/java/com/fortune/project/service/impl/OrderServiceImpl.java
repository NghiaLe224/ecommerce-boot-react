package com.fortune.project.service.impl;

import com.fortune.project.dto.request.order.*;
import com.fortune.project.dto.response.common.ApiResponse;
import com.fortune.project.dto.response.common.PagingResponse;
import com.fortune.project.entity.*;
import com.fortune.project.exception.ApiException;
import com.fortune.project.exception.ResourceNotFoundException;
import com.fortune.project.repository.OrderRepository;
import com.fortune.project.service.*;
import com.fortune.project.util.AuthUtil;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;
    private final PaymentService paymentService;
    private final ProductService productService;
    private final AddressService addressService;
    private final AuthUtil authUtil;
    private final ShipmentService shipmentService;
    private final ModelMapper modelMapper;

    public OrderResponse createOrder(CreateOrderRequest request) {
        // Get current user
        UserEntity user = authUtil.loggedInUser();

        // Validate product and calculate total amount
        Double subtotal = calculateSubtotal(request.getItems());
        Double shippingFee = shipmentService.calculateShippingFee(request.getShippingAddressId(), subtotal);
        Double total = subtotal + shippingFee;

        // Create OrderEntity
        OrderEntity order = new OrderEntity();
        order.setCustomer(user);
        order.setStatus(OrderStatus.PENDING);
        order.setSubtotal(subtotal);
        order.setShippingFee(shippingFee);
        order.setTotal(total);
        order.setNote(request.getNote());

        // Gán địa chỉ giao hàng
        AddressEntity address = addressService.findById(request.getShippingAddressId());
        order.setAddress(address);

        // Tạo OrderItemEntity
        List<OrderItemEntity> items = productService.buildOrderItems(request.getItems(), order);
        order.setItems(items);

        // Lưu order
        orderRepository.save(order);

        // Tạo PaymentEntity
        PaymentResponse paymentResponse = paymentService.createPayment(order, request.getPaymentMethod());

        return OrderResponse.from(order, Collections.singletonList(paymentResponse));
    }

    public PagingResponse<OrderResponse> getOrdersForCurrentUser(Pageable pageable) {
        UserEntity user = authUtil.loggedInUser();

        Page<OrderEntity> orders = orderRepository.findByCustomer(user, pageable);

        Page<OrderResponse> orderResponses = orders.map(order ->
                OrderResponse.from(
                        order,
                        order.getPayments()
                                .stream()
                                .map(PaymentResponse::from)
                                .toList()
                )
        );

        return new PagingResponse<>(orderResponses);
    }


    public OrderResponse getOrderDetail(Long orderId) {
        OrderEntity order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order", "order id", orderId));
        List<PaymentResponse> paymentResponses = order.getPayments().stream()
                .map(PaymentResponse::from).toList();
        return OrderResponse.from(order, paymentResponses);
    }

    public void cancelOrder(Long orderId) {
        OrderEntity order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order", "order id", orderId));
        if (order.getStatus() != OrderStatus.PENDING) {
            throw new ApiException("Only pending orders can be cancelled");
        }
        order.setStatus(OrderStatus.CANCELLED);
        orderRepository.save(order);
    }

    @Override
    public PagingResponse<OrderResponse> getAllOrders(Pageable pageable) {
        Page<OrderEntity> orders = orderRepository.findAll(pageable);
        Page<OrderResponse> res = orders
                .map(o -> OrderResponse.from(o, o.getPayments().stream().map(PaymentResponse::from).toList()));
        return new PagingResponse<>(res);
    }

    @Override
    public ApiResponse<?> updateOrderStatus(UpdateOrderStatusRequest request) {
        OrderEntity order = orderRepository.findById(request.getOrderId()).orElseThrow(() -> new ResourceNotFoundException("Order", "id", request.getOrderId()));

        order.setStatus(request.getOrderStatus());
        orderRepository.save(order);

        // Return orderId and updated status back
        Map<String, Object> responseData = new HashMap<>();
        responseData.put("orderId", order.getId());
        responseData.put("orderStatus", order.getStatus());

        return new ApiResponse<>("Update order status successfully", responseData, LocalDateTime.now());
    }

    @Override
    public PagingResponse<OrderResponse> getOrdersForSeller(Pageable pageable, Long sellerId) {
        Page<OrderEntity> orders = orderRepository.findOrdersBySellerId(sellerId, pageable);
        Page<OrderResponse> response = orders.map(order ->
                OrderResponse.from(order, order.getPayments().stream()
                        .map(PaymentResponse::from)
                        .toList()));
        return new PagingResponse<>(response);
    }


    private Double calculateSubtotal(List<OrderItemRequest> items) {
        return items.stream()
                .mapToDouble(item -> {
                    ProductEntity product = productService.findById(item.getProductId());
                    return product.getPrice() * item.getQuantity();
                })
                .sum();
    }
}
