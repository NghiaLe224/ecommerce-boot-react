package com.fortune.project.controller;

import com.fortune.project.dto.request.product.ProductRequest;
import com.fortune.project.dto.response.common.ApiResponse;
import com.fortune.project.dto.response.common.PagingResponse;
import com.fortune.project.dto.response.product.ProductResponse;
import com.fortune.project.service.ProductService;
import com.fortune.project.util.PaginationUtils;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

import static com.fortune.project.constant.AppConstant.DEFAULT_SORT_BY_ID;
import static com.fortune.project.constant.ProductConstant.*;

@RestController
@RequestMapping("/api")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @PostMapping("/admin/categories/{categoryId}/product")
    public ResponseEntity<?> createProduct(@PathVariable Long categoryId,
                                           @RequestPart("product") ProductRequest request,
                                           @RequestPart(value = "imageFile", required = false) MultipartFile imageFile
                                          ) {
        var response = productService.createProduct(categoryId, request, imageFile);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/public/products")
    public ResponseEntity<?> getAllProducts(
            @RequestParam(name = "pageNumber", defaultValue = DEFAULT_PAGE + "", required = false) Integer page,
            @RequestParam(name = "pageSize", defaultValue = DEFAULT_SIZE + "", required = false) Integer size,
            @RequestParam(name = "sortBy", defaultValue = DEFAULT_SORT_BY_ID, required = false) String sortBy,
            @RequestParam(name = "sortOrder", defaultValue = DEFAULT_SORT_DIR, required = false) String sortDir,
            @RequestParam(name = "category", required = false) String category,
            @RequestParam(name = "keyword", required = false) String keyword
    ) {
        Pageable pageable = PaginationUtils.createPageable(page, size, sortBy, sortDir);
        var response = productService.getAllProducts(category, keyword, pageable);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/admin/products")
    public ResponseEntity<?> getAllProductsAdmin(
            @RequestParam(name = "pageNumber", defaultValue = DEFAULT_PAGE + "", required = false) Integer page,
            @RequestParam(name = "pageSize", defaultValue = DEFAULT_SIZE + "", required = false) Integer size,
            @RequestParam(name = "sortBy", defaultValue = DEFAULT_SORT_BY_ID, required = false) String sortBy,
            @RequestParam(name = "sortOrder", defaultValue = DEFAULT_SORT_DIR, required = false) String sortDir,
            @RequestParam(name = "category", required = false) String category,
            @RequestParam(name = "keyword", required = false) String keyword
    ) {
        Pageable pageable = PaginationUtils.createPageable(page, size, sortBy, sortDir);
        var response = productService.getAllProducts(category, keyword, pageable);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

//    @GetMapping("/public/categories/{categoryId}/products")
//    public ResponseEntity<?> getProductsByCategoryId(
//            @PathVariable Long categoryId,
//            @RequestParam(name = "pageNumber", defaultValue = DEFAULT_PAGE + "", required = false) Integer page,
//            @RequestParam(name = "pageSize", defaultValue = DEFAULT_SIZE + "", required = false) Integer size,
//            @RequestParam(name = "sortBy", defaultValue = DEFAULT_SORT_BY, required = false) String sortBy,
//            @RequestParam(name = "sortOrder", defaultValue = DEFAULT_SORT_DIR, required = false) String sortDir
//    ) {
//        Pageable pageable = PaginationUtils.createPageable(page, size, sortBy, sortDir);
//        ApiResponse<PagingResponse<ProductResponse>> response = productService.getAllProductsByCategoryId(categoryId, pageable);
//        return new ResponseEntity<>(response, HttpStatus.OK);
//    }
//
//    @GetMapping("/public/products/keyword/{keyword}")
//    public ResponseEntity<?> getProductsByKeywords(
//            @PathVariable String keyword,
//            @RequestParam(name = "pageNumber", defaultValue = DEFAULT_PAGE + "", required = false) Integer page,
//            @RequestParam(name = "pageSize", defaultValue = DEFAULT_SIZE + "", required = false) Integer size,
//            @RequestParam(name = "sortBy", defaultValue = DEFAULT_SORT_BY, required = false) String sortBy,
//            @RequestParam(name = "sortOrder", defaultValue = DEFAULT_SORT_DIR, required = false) String sortDir
//    ) {
//        Pageable pageable = PaginationUtils.createPageable(page, size, sortBy, sortDir);
//        ApiResponse<PagingResponse<ProductResponse>> response
//                = productService.getAllProductsByKeyword(keyword, pageable);
//        return new ResponseEntity<>(response, HttpStatus.FOUND);
//    }

    @PutMapping("/admin/products/{productId}")
    public ResponseEntity<ApiResponse<ProductResponse>> updateProductById(
            @PathVariable Long productId,
            @RequestBody ProductRequest request
    ) {
        ApiResponse<ProductResponse> response
                = productService.updateProductByProductId(productId, request);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping("/admin/products/{productId}")
    public ResponseEntity<ApiResponse<Void>> deleteProductById(@PathVariable Long productId) {
        ApiResponse<Void> response
                = productService.deleteProductById(productId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PutMapping("/admin/products/{productId}/image")
    public ResponseEntity<ApiResponse<ProductResponse>> updateProductImage(
            @PathVariable long productId,
            @RequestParam("image") MultipartFile image
    ) throws IOException {
        ApiResponse<ProductResponse> response
                = productService.updateProductImage(productId, image);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/seller/products")
    public ResponseEntity<?> getAllSellerProducts(
            @RequestParam(name = "pageNumber", defaultValue = DEFAULT_PAGE + "", required = false) Integer page,
            @RequestParam(name = "pageSize", defaultValue = DEFAULT_SIZE + "", required = false) Integer size,
            @RequestParam(name = "sortBy", defaultValue = DEFAULT_SORT_BY_ID, required = false) String sortBy,
            @RequestParam(name = "sortOrder", defaultValue = DEFAULT_SORT_DIR, required = false) String sortDir,
            @RequestParam(name = "category", required = false) String category,
            @RequestParam(name = "keyword", required = false) String keyword
    ) {
        Pageable pageable = PaginationUtils.createPageable(page, size, sortBy, sortDir);
        ApiResponse<PagingResponse<ProductResponse>>  response = productService.getAllSellerProducts(category, keyword, pageable);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/seller/categories/{categoryId}/product")
    public ResponseEntity<?> createSellerProduct(@PathVariable Long categoryId,
                                           @RequestPart("product") ProductRequest request,
                                           @RequestPart(value = "imageFile", required = false) MultipartFile imageFile
    ) {
        var response = productService.createProduct(categoryId, request, imageFile);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PutMapping("/seller/products/{productId}")
    public ResponseEntity<ApiResponse<ProductResponse>> updateSellerProductById(
            @PathVariable Long productId,
            @RequestBody ProductRequest request
    ) {
        ApiResponse<ProductResponse> response
                = productService.updateProductByProductId(productId, request);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping("/seller/products/{productId}")
    public ResponseEntity<ApiResponse<Void>> deleteSellerProductById(@PathVariable Long productId) {
        ApiResponse<Void> response
                = productService.deleteProductById(productId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PutMapping("/seller/products/{productId}/image")
    public ResponseEntity<ApiResponse<ProductResponse>> updateSellerProductImage(
            @PathVariable long productId,
            @RequestParam("image") MultipartFile image
    ) throws IOException {
        ApiResponse<ProductResponse> response
                = productService.updateProductImage(productId, image);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }



}
