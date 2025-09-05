package com.fortune.project.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter @Setter
@Table(name = "carts")
public class CartEntity extends BaseEntity{

    @OneToOne
    @JoinColumn(name = "user_id")
    private UserEntity user;

    @OneToMany(mappedBy = "cart", cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE}, orphanRemoval = true)
    private List<CartItemEntity> cartItems = new ArrayList<>();

    private Double totalPrice = 0.0;

    public void removeItem(Long productId) {
        cartItems.removeIf(cartItem -> cartItem.getProduct().getId().equals(productId));
    }

    public void addItem(ProductEntity product, int quantity) {
        CartItemEntity cartItem = new CartItemEntity();
        cartItem.setCart(this);
        cartItem.setProduct(product);
        cartItem.setQuantity(quantity);
        cartItem.setSnapshotPrice(product.getSpecialPrice());
        cartItem.setSnapshotName(product.getName());
        cartItem.setSubTotal(product.getSpecialPrice() * quantity);
        cartItem.setFinalPrice(product.getSpecialPrice() * quantity);
        cartItems.add(cartItem);
    }

    public void recalculateTotalPrice() {
        this.totalPrice = cartItems.stream()
                .mapToDouble(CartItemEntity::getFinalPrice)
                .sum();
    }

}
