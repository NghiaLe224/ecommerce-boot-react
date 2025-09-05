package com.fortune.project.service;

import com.fortune.project.dto.request.category.CategoryCreateRequest;
import com.fortune.project.dto.request.category.CategoryUpdateRequest;
import com.fortune.project.dto.response.common.ApiResponse;
import com.fortune.project.dto.response.category.CategoryResponse;
import com.fortune.project.dto.response.common.PagingResponse;
import org.springframework.data.domain.Pageable;

public interface CategoryService {
    ApiResponse<PagingResponse<CategoryResponse>> getAllCategories(Pageable pageable);
    ApiResponse<CategoryResponse> createCategory(CategoryCreateRequest category);
    ApiResponse<CategoryResponse> deleteCategory(Long id);
    ApiResponse<CategoryResponse> updateCategory(Long id, CategoryUpdateRequest category);
}
