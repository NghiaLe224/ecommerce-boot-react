package com.fortune.project.dto.response.cart;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
public class CartResponse <T>{
    private Long cartId;
    private Long userId;
    private T items;
    private Double totalPrice;
}
