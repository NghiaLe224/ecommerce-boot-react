package com.fortune.project.service;

import com.fortune.project.dto.request.order.OrderItemRequest;
import com.fortune.project.dto.request.product.ProductRequest;
import com.fortune.project.dto.response.common.ApiResponse;
import com.fortune.project.dto.response.common.PagingResponse;
import com.fortune.project.dto.response.product.ProductResponse;
import com.fortune.project.entity.OrderEntity;
import com.fortune.project.entity.OrderItemEntity;
import com.fortune.project.entity.ProductEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface ProductService {

    ApiResponse<ProductResponse> createProduct(Long categoryId, ProductRequest request, MultipartFile imageFile);

    ApiResponse<PagingResponse<ProductResponse>>  getAllProducts(String category, String keyword, Pageable pageable);

    ApiResponse<PagingResponse<ProductResponse>> getAllProductsByCategoryId(Long categoryId, Pageable pageable);

    ApiResponse<PagingResponse<ProductResponse>> getAllProductsByKeyword(String keyword, Pageable pageable);

    ApiResponse<ProductResponse> updateProductByProductId(Long productId, ProductRequest request);

    ApiResponse<Void> deleteProductById(Long productId);

    ApiResponse<ProductResponse> updateProductImage(long productId, MultipartFile image) throws IOException;

    ProductEntity findById(Long productId);

    List<OrderItemEntity> buildOrderItems(List<OrderItemRequest> items, OrderEntity order);

    ApiResponse<PagingResponse<ProductResponse>>  getAllSellerProducts(String category, String keyword, Pageable pageable);
}
