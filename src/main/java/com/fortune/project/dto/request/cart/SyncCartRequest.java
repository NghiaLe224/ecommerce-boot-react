package com.fortune.project.dto.request.cart;

import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class SyncCartRequest {
    @NotEmpty(message = "Cart items cannot be empty")
    private List<CartItemDTO> items;
}
