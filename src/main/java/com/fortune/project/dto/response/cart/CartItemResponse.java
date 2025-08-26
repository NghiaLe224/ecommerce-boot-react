package com.fortune.project.dto.response.cart;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CartItemResponse {

    private Long productId;
    private String name;
    private String imageUrl;
    private Double snapshotPrice;
    private Integer quantity;
    private Long discountPercent;
    private Double subTotal;
    private Boolean priceChanged;
    private Boolean deleted;
}
