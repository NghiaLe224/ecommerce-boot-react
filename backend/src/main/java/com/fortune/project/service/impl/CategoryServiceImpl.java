package com.fortune.project.service.impl;

import com.fortune.project.entity.CategoryEntity;
import com.fortune.project.dto.request.category.CategoryCreateRequest;
import com.fortune.project.dto.request.category.CategoryUpdateRequest;
import com.fortune.project.dto.response.common.ApiResponse;
import com.fortune.project.dto.response.category.CategoryResponse;
import com.fortune.project.dto.response.common.PagingResponse;
import com.fortune.project.exception.ApiException;
import com.fortune.project.exception.ResourceNotFoundException;
import com.fortune.project.repository.CategoryRepository;
import com.fortune.project.service.CategoryService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository repo;
    private final ModelMapper modelMapper;

    @Autowired
    public CategoryServiceImpl(CategoryRepository repo, ModelMapper modelMapper) {
        this.repo = repo;
        this.modelMapper = modelMapper;
    }

    @Override
    public ApiResponse<PagingResponse<CategoryResponse>> getAllCategories(Pageable pageable) {
        Page<CategoryEntity> categories = repo.findAll(pageable);
        if (categories.isEmpty()) throw new ApiException("Have no category created till now", "Categories is empty");
        Page<CategoryResponse> dtoPage = categories.map(e -> modelMapper.map(
                e, CategoryResponse.class
        ));
        return new ApiResponse<>("Categories fetched success",
                new PagingResponse<>(dtoPage), LocalDateTime.now());
    }

    @Override
    public ApiResponse<CategoryResponse> createCategory(CategoryCreateRequest category) {
        if (repo.findByName(category.getCategoryName()) != null) {
            throw new ApiException("CategoryEntity with name %s already exist!!"
                    .formatted(category.getCategoryName()), "Duplicated category name");
        }
        CategoryEntity createdCategoryEntity = new CategoryEntity();
        createdCategoryEntity.setName(category.getCategoryName());
        createdCategoryEntity.setDescription(category.getCategoryDescription());
        CategoryEntity saved = repo.save(createdCategoryEntity);
        return new ApiResponse<>("CategoryEntity created",
                modelMapper.map(saved, CategoryResponse.class),
                LocalDateTime.now());
    }

    @Override
    public ApiResponse<CategoryResponse> deleteCategory(Long id) {
        if (!repo.existsById(id)) throw new ResourceNotFoundException("CategoryEntity", "categoryId", id);
        CategoryEntity categoryToDeleted = repo.findById(id).get();
        repo.deleteById(id);
        return new ApiResponse<>("CategoryEntity deleted successfully!",
                modelMapper.map(categoryToDeleted, CategoryResponse.class)
                , LocalDateTime.now());
    }

    @Override
    public ApiResponse<CategoryResponse> updateCategory(Long id, CategoryUpdateRequest category) {
        CategoryEntity categoryEntityToUpdate = repo.findById(id).orElseThrow(() ->
                new ResourceNotFoundException(
                        "CategoryEntity", "categoryId", id));
        ;
        categoryEntityToUpdate.setName(category.getCategoryName());
        categoryEntityToUpdate.setDescription(category.getCategoryDescription());
        CategoryEntity updatedCategoryEntity = repo.save(categoryEntityToUpdate);
        return new ApiResponse<>("CategoryEntity updated successfully",
                modelMapper.map(updatedCategoryEntity, CategoryResponse.class)
               , LocalDateTime.now());
    }


}
