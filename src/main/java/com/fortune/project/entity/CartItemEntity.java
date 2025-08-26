package com.fortune.project.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "cart_items")
@AllArgsConstructor
@NoArgsConstructor
@Getter @Setter
public class CartItemEntity extends BaseEntity{

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cart_id")
    private CartEntity cart;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private ProductEntity product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "promotion_id")
    private PromotionEntity appliedPromotion;

    private Integer quantity;
    private Double snapshotPrice;
    private String snapshotName;
    private Double finalPrice;
    private Double subTotal;

    public void updateQuantity(int quantity) {
        this.setQuantity(quantity);
        this.setFinalPrice(this.snapshotPrice * quantity);
        this.setSubTotal(this.snapshotPrice * quantity);
    }
}
