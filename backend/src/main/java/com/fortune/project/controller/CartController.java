package com.fortune.project.controller;

import com.fortune.project.constant.AppConstant;
import com.fortune.project.dto.request.cart.CartItemQuantityUpdateDTO;
import com.fortune.project.dto.request.cart.SyncCartRequest;
import com.fortune.project.dto.response.common.ApiResponse;
import com.fortune.project.security.service.UserDetailsImpl;
import com.fortune.project.service.CartService;
import com.fortune.project.util.PaginationUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;


    @PostMapping("carts/products/{productid}/quantity/{quantity}")
    public ResponseEntity<?> addToCart(
            @PathVariable(name = "productid") Long productId,
            @PathVariable Integer quantity,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        return new ResponseEntity<ApiResponse<?>>(cartService.addToCart(productId, quantity, userDetails.getEmail()), HttpStatus.CREATED);
    }

    @GetMapping("/carts")
    public ResponseEntity<?> viewAllCart(
            @RequestParam(defaultValue = AppConstant.DEFAULT_PAGE + "", required = false) int page,
            @RequestParam(defaultValue = AppConstant.DEFAULT_SIZE + "", required = false) int size,
            @RequestParam(defaultValue = AppConstant.DEFAULT_SORT_DIR, required = false) String sortDir,
            @RequestParam(defaultValue = AppConstant.DEFAULT_SORT_BY_ID, required = false) String sortBy
    ) {
        Pageable pageable = PaginationUtils.createPageable(page, size, sortBy, sortDir);
        ApiResponse<?> cartResponse = cartService.viewAllCart(pageable);
        return new ResponseEntity<>(cartResponse, HttpStatus.OK);
    }

    @GetMapping("/carts/users/cart")
    public ResponseEntity<?> viewCart(
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        ApiResponse<?> response = cartService.viewCart(userDetails.getId());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PutMapping("/carts/items")
    public ResponseEntity<Void> updateItemQuantity(@AuthenticationPrincipal UserDetailsImpl user,
                                                   @Valid @RequestBody CartItemQuantityUpdateDTO request) {
        cartService.updateItemQuantity(user.getId(), request.getProductId(), request.getQuantity());
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("carts/items/{productId}")
    public ResponseEntity<Void> removeItem(@AuthenticationPrincipal UserDetailsImpl user,
                                           @PathVariable Long productId) {
        cartService.removeItem(user.getId(), productId);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/carts/sync")
    public ResponseEntity<Void> syncCart(@AuthenticationPrincipal UserDetailsImpl user,
                                         @RequestBody @Valid SyncCartRequest request) {
        cartService.syncCart(user.getId(), request.getItems());
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/carts/items")
    public ResponseEntity<Void> removeAllItems(@AuthenticationPrincipal UserDetailsImpl user){
        cartService.removeAllItems(user.getId());
        return ResponseEntity.noContent().build();
    }
}
