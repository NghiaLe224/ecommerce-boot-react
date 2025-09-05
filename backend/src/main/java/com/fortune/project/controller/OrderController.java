package com.fortune.project.controller;

import com.fortune.project.dto.request.order.CreateOrderRequest;
import com.fortune.project.dto.request.order.OrderResponse;
import com.fortune.project.dto.request.order.UpdateOrderStatusRequest;
import com.fortune.project.dto.response.common.PagingResponse;
import com.fortune.project.service.OrderService;
import com.fortune.project.util.AuthUtil;
import com.fortune.project.util.PaginationUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.fortune.project.constant.AppConstant.DEFAULT_SORT_BY_ID;
import static com.fortune.project.constant.ProductConstant.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private final AuthUtil authUtil;

    @PostMapping("/orders")
    public ResponseEntity<OrderResponse> createOrder(@Valid @RequestBody CreateOrderRequest request) {
        OrderResponse response = orderService.createOrder(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/orders")
    public ResponseEntity<PagingResponse<OrderResponse>> getOrders(
            @RequestParam(name = "pageNumber", defaultValue = DEFAULT_PAGE + "", required = false) Integer page,
            @RequestParam(name = "pageSize", defaultValue = DEFAULT_SIZE + "", required = false) Integer size,
            @RequestParam(name = "sortBy", defaultValue = DEFAULT_SORT_BY_ID, required = false) String sortBy,
            @RequestParam(name = "sortOrder", defaultValue = DEFAULT_SORT_DIR, required = false) String sortDir
    ) {
        Pageable pageable = PaginationUtils.createPageable(page, size, sortBy, sortDir);
        PagingResponse<OrderResponse> orders = orderService.getOrdersForCurrentUser(pageable);
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/orders/{orderId}")
    public ResponseEntity<OrderResponse> getOrderDetail(@PathVariable Long orderId) {
        OrderResponse response = orderService.getOrderDetail(orderId);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/orders/{orderId}")
    public ResponseEntity<Void> cancelOrder(@PathVariable Long orderId) {
        orderService.cancelOrder(orderId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/admin/orders")
    public ResponseEntity<?> getAllOrders(
            @RequestParam(name = "pageNumber", defaultValue = DEFAULT_PAGE + "", required = false) Integer page,
            @RequestParam(name = "pageSize", defaultValue = DEFAULT_SIZE + "", required = false) Integer size,
            @RequestParam(name = "sortBy", defaultValue = DEFAULT_SORT_BY_ID, required = false) String sortBy,
            @RequestParam(name = "sortOrder", defaultValue = DEFAULT_SORT_DIR, required = false) String sortDir
    ) {
        Pageable pageable = PaginationUtils.createPageable(page, size, sortBy, sortDir);
        PagingResponse<OrderResponse> res = orderService.getAllOrders(pageable);
        return ResponseEntity.ok(res);
    }

    @PutMapping("/admin/orders/status")
    public ResponseEntity<?> updateOrderStatus(
            @RequestBody UpdateOrderStatusRequest request
    ) {
        var res = orderService.updateOrderStatus(request);
        return ResponseEntity.ok(res);
    }

    @GetMapping("/seller/orders")
    public ResponseEntity<?> getAllSellerOrders(
            @RequestParam(name = "pageNumber", defaultValue = DEFAULT_PAGE + "", required = false) Integer page,
            @RequestParam(name = "pageSize", defaultValue = DEFAULT_SIZE + "", required = false) Integer size,
            @RequestParam(name = "sortBy", defaultValue = DEFAULT_SORT_BY_ID, required = false) String sortBy,
            @RequestParam(name = "sortOrder", defaultValue = DEFAULT_SORT_DIR, required = false) String sortDir
    ) {
        Pageable pageable = PaginationUtils.createPageable(page, size, sortBy, sortDir);
        Long sellerId = authUtil.loggedInId();
        PagingResponse<OrderResponse> res = orderService.getOrdersForSeller(pageable, sellerId);
        return ResponseEntity.ok(res);
    }

    @PutMapping("/seller/orders/status")
    public ResponseEntity<?> updateSellerOrderStatus(
            @RequestBody UpdateOrderStatusRequest request
    ) {
        var res = orderService.updateOrderStatus(request);
        return ResponseEntity.ok(res);
    }


}

