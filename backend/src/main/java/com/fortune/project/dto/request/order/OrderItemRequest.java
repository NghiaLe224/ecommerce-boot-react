package com.fortune.project.dto.request.order;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter @Setter
public class OrderItemRequest {
    @NotNull(message = "Product id cannot be null")
    private Long productId;

    @Min(value = 1, message = "Item quantity must not less than 1")
    private int quantity;

    private String note; // Optional
}
