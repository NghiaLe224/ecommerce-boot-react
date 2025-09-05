package com.fortune.project.dto.response.category;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class CategoryResponse {
    private Long categoryId;
    private String categoryName;
    private String categoryDescription;

}
