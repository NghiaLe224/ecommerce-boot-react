package com.fortune.project.controller;

import com.fortune.project.dto.request.category.CategoryCreateRequest;
import com.fortune.project.dto.request.category.CategoryUpdateRequest;
import com.fortune.project.dto.response.common.ApiResponse;
import com.fortune.project.dto.response.category.CategoryResponse;
import com.fortune.project.service.CategoryService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.fortune.project.constant.AppConstant.DEFAULT_SORT_BY_ID;
import static com.fortune.project.constant.CategoryConstant.*;

@RestController
@RequestMapping("/api")
public class CategoryController {

    private final CategoryService categoryService;

    @Autowired
    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping("/public/categories")
    public ResponseEntity<?> getAllCategories(
            @RequestParam(defaultValue = DEFAULT_PAGE + "", required = false) Integer page,
            @RequestParam(defaultValue = DEFAULT_SIZE + "", required = false) Integer size,
            @RequestParam(defaultValue = "name", required = false) String sortBy,
            @RequestParam(defaultValue = DEFAULT_SORT_DIR, required = false) String sortDir
    ) {
        Sort sort = sortDir.equalsIgnoreCase("desc") ?
                Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        return ResponseEntity.ok(categoryService.getAllCategories(pageable));
    }

    @PostMapping("/admin/categories")
    public ResponseEntity<?> createCategory(@Valid @RequestBody CategoryCreateRequest category) {
        ApiResponse<CategoryResponse> response = categoryService.createCategory(category);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @DeleteMapping("/admin/categories/{id}")
    public ResponseEntity<?> deleteCategory(@PathVariable Long id) {
        return ResponseEntity.ok(categoryService.deleteCategory(id));
    }

    @PutMapping("/admin/categories/{id}")
    public ResponseEntity<?> updateCategory(@Valid @RequestBody CategoryUpdateRequest category,
                                                 @PathVariable Long id) {
        var res = categoryService.updateCategory(id, category);
        return ResponseEntity.ok(res);
    }

}
