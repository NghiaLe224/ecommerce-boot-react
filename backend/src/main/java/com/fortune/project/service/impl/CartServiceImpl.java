package com.fortune.project.service.impl;

import com.fortune.project.dto.request.cart.CartItemDTO;
import com.fortune.project.dto.response.cart.CartItemResponse;
import com.fortune.project.dto.response.cart.CartResponse;
import com.fortune.project.dto.response.common.ApiResponse;
import com.fortune.project.dto.response.common.PagingResponse;
import com.fortune.project.entity.CartEntity;
import com.fortune.project.entity.CartItemEntity;
import com.fortune.project.entity.ProductEntity;
import com.fortune.project.exception.ApiException;
import com.fortune.project.exception.ResourceNotFoundException;
import com.fortune.project.repository.CartItemRepository;
import com.fortune.project.repository.CartRepository;
import com.fortune.project.repository.ProductRepository;
import com.fortune.project.repository.UserRepository;
import com.fortune.project.service.CartService;
import com.fortune.project.util.AuthUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {

    private final ProductRepository productRepository;
    private final CartRepository cartRepository;
    private final AuthUtil authUtil;
    private final CartItemRepository cartItemRepository;
    private final UserRepository userRepository;

    @Override
    public ApiResponse<?> addToCart(Long productId, Integer quantity, String email) {
        // 1. Tìm sản phẩm
        ProductEntity product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "Product id", productId));

        if (product.getStock() < quantity) {
            throw new ApiException("Sufficient stock");
        }

        // 2. Lấy/tạo giỏ hàng người dùng
        CartEntity cart = cartRepository.findByUser_email(email)
                .orElseGet(() -> {
                    CartEntity newCart = new CartEntity();
                    newCart.setUser(authUtil.loggedInUser());
                    return cartRepository.save(newCart);
                });

        // 3. Kiểm tra sản phẩm đã có trong giỏ chưa
        Optional<CartItemEntity> optionalItem = cart.getCartItems().stream()
                .filter(i -> i.getProduct().getId().equals(productId))
                .findFirst();

        CartItemEntity cartItem = optionalItem.orElseGet(() -> {
            CartItemEntity newItem = new CartItemEntity();
            newItem.setProduct(product);
            newItem.setSnapshotPrice(product.getSpecialPrice());
            newItem.setCart(cart);
            newItem.setQuantity(0); // bắt đầu từ 0
            newItem.setSnapshotName(product.getName());
            return newItem;
        });

        // 4. Cập nhật số lượng
        int updatedQuantity = cartItem.getQuantity() + quantity;
        cartItem.setQuantity(updatedQuantity);

        // 5. Tính lại subTotal và finalPrice
        double updatedPrice = cartItem.getSnapshotPrice() * updatedQuantity;
        cartItem.setSubTotal(updatedPrice);
        cartItem.setFinalPrice(updatedPrice); // nếu có chiết khấu riêng thì xử lý khác

        cartItemRepository.save(cartItem);

        // 6. Tính lại tổng giỏ
        List<CartItemEntity> allItems = cartItemRepository.findByCart_Id(cart.getId());

        double totalPrice = allItems.stream()
                .mapToDouble(item -> item.getSnapshotPrice() * item.getQuantity())
                .sum();

        cart.setTotalPrice(totalPrice);
        cartRepository.save(cart);

        // 7. Build response
        List<CartItemResponse> cartItems = allItems.stream().map(item -> {
            CartItemResponse resp = new CartItemResponse();
            resp.setProductId(item.getProduct().getId());
            resp.setName(item.getProduct().getName());
            resp.setImageUrl(item.getProduct().getImage());
            resp.setSnapshotPrice(item.getSnapshotPrice());
            resp.setQuantity(item.getQuantity());
            resp.setSubTotal(item.getSnapshotPrice() * item.getQuantity());
            return resp;
        }).toList();

        CartResponse<List<CartItemResponse>> cartResponse = new CartResponse<>(
                cart.getId(),
                cart.getUser().getId(),
                cartItems,
                totalPrice
        );

        return new ApiResponse<>("Product added success", cartResponse, LocalDateTime.now());
    }


    @Override
    public ApiResponse<?> viewCart(Long id) {
        CartEntity cart = cartRepository.findByUser_id(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cart", "user id", id));

        List<CartItemEntity> CartItems = cartItemRepository.findByCart_id(cart.getId());

        List<CartItemResponse> cartItemResponses = CartItems.stream()
                .map(cartItem -> {
                    ProductEntity product = cartItem.getProduct();
                    boolean deleted = (product == null);
                    Double currentPrice = deleted ? cartItem.getSnapshotPrice() :product.getSpecialPrice();
                    boolean priceChanged = !deleted && !cartItem.getSnapshotPrice().equals(currentPrice);

                    Double subTotal = cartItem.getSnapshotPrice() * cartItem.getQuantity();

                    return new CartItemResponse(
                            deleted ? null : product.getId(),
                            deleted ? cartItem.getSnapshotName() + " (Deleted)" : product.getName(),
                            deleted ? "/uploads/images/placeholder.png" : product.getImage(),
                            cartItem.getSnapshotPrice(),
                            cartItem.getQuantity(),
                            null,
                            subTotal,
                            priceChanged,
                            deleted
                    );
                }).toList();

        Double totalPrice = cart.getCartItems().stream()
                .mapToDouble(item -> item.getSnapshotPrice() * item.getQuantity())
                .sum();

        CartResponse<List<CartItemResponse>> cartResponse = new CartResponse<>(
                cart.getId(),
                cart.getUser().getId(),
                cartItemResponses,
                totalPrice
        );
        return new ApiResponse<>("Fetched cart successfully", cartResponse, LocalDateTime.now());
    }

    @Override
    public ApiResponse<?> viewAllCart(Pageable pageable) {
        Page<CartEntity> carts = cartRepository.findAll(pageable);

        List<CartResponse<List<CartItemResponse>>> cartResponses = carts.getContent().stream()
                .map(cart -> {
                    List<CartItemResponse> items = cart.getCartItems().stream()
                            .map(cartItem -> {
                                ProductEntity product = cartItem.getProduct();
                                Boolean deleted = (product == null);
                                Double currentPrice = deleted ? cartItem.getSnapshotPrice() : product.getSpecialPrice();
                                boolean priceChanged = !deleted && !cartItem.getSnapshotPrice().equals(currentPrice);

                                Double subTotal = cartItem.getSnapshotPrice() * cartItem.getQuantity();

                                return new CartItemResponse(
                                        cartItem.getId(),
                                        deleted ? cartItem.getSnapshotName() : product.getName(),
                                        deleted ? "/img/default.png" : product.getImage(),
                                        cartItem.getSnapshotPrice(),
                                        cartItem.getQuantity(),
                                        null,
                                        subTotal,
                                        priceChanged,
                                        deleted
                                );
                            }).toList();

                    Double totalPrice = items.stream()
                            .mapToDouble(CartItemResponse::getSubTotal)
                            .sum();

                    return new CartResponse<>(
                            cart.getId(),
                            cart.getUser().getId(),
                            items,
                            totalPrice
                    );
                }).toList();

        Page<CartResponse<List<CartItemResponse>>> pageResult = new PageImpl<>(
                cartResponses,
                pageable,
                carts.getTotalElements()
        );

        PagingResponse<CartResponse<List<CartItemResponse>>> pagingResponse = new PagingResponse<>(pageResult);

        return new ApiResponse<>("Fetched all cart successfully", pagingResponse, LocalDateTime.now());
    }

    @Override
    public void updateItemQuantity(Long id, Long productId, int quantity) {
        CartEntity cart = cartRepository.findByUser_id(id).orElseThrow(() -> new ResourceNotFoundException("Cart", "user id", id));

         Optional<CartItemEntity> optionalItem = cart.getCartItems().stream().filter(cartItem -> cartItem.getProduct().getId().equals(productId)).findFirst();

        if(optionalItem.isPresent()){
            if(quantity <= 0){
                cart.removeItem(productId);
            }else{
                optionalItem.get().updateQuantity(quantity);
            }
        }else{
            if(quantity > 0){
                ProductEntity product = productRepository.findById(productId).orElseThrow(() -> new ResourceNotFoundException("Product", "product id", productId));
                cart.addItem(product, quantity);
            }
        }

        cartRepository.save(cart);
    }

    @Override
    public void removeItem(Long id, Long productId) {
        CartEntity cart = cartRepository.findByUser_id(id).orElseThrow(() -> new ResourceNotFoundException("Cart", "user id", id));

        boolean removed = cart.getCartItems().removeIf(i -> i.getProduct().getId().equals(productId));

        if (!removed) {
            throw new ResourceNotFoundException("CartItem", "product id", productId);
        }
    }

    @Override
    public void syncCart(Long id, List<CartItemDTO> items) {
        CartEntity cart = cartRepository.findByUser_id(id)
                .orElseGet(() -> {
                    CartEntity newCart = new CartEntity();
                    newCart.setUser(userRepository.getReferenceById(id));
                    return newCart;
                });

        cart.getCartItems().clear();

        for (CartItemDTO itemDTO : items) {
            ProductEntity product = productRepository.findById(itemDTO.getProductId())
                    .orElseThrow(() -> new ResourceNotFoundException("Product", "id", itemDTO.getProductId()));

            cart.addItem(product, itemDTO.getQuantity());
        }

        cart.recalculateTotalPrice();
        cartRepository.save(cart);
    }

    @Override
    public void removeAllItems(Long id) {
        CartEntity cart = cartRepository.findByUser_id(id).orElseThrow(() -> new ResourceNotFoundException("Cart", "user id", id));
        if (cart.getCartItems().isEmpty()) {
            cart.setTotalPrice(0.0);
            return;
        }
        cart.getCartItems().forEach(item -> item.setCart(null));
        cart.getCartItems().clear();
        cart.recalculateTotalPrice();
    }
}
