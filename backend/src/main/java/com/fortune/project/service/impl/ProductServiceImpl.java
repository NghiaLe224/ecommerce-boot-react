package com.fortune.project.service.impl;

import com.fortune.project.constant.ProductConstant;
import com.fortune.project.dto.request.order.OrderItemRequest;
import com.fortune.project.dto.request.product.ProductRequest;
import com.fortune.project.dto.response.common.ApiResponse;
import com.fortune.project.dto.response.common.PagingResponse;
import com.fortune.project.dto.response.product.ProductResponse;
import com.fortune.project.entity.*;
import com.fortune.project.exception.ApiException;
import com.fortune.project.exception.ResourceNotFoundException;
import com.fortune.project.repository.CategoryRepository;
import com.fortune.project.repository.ProductRepository;
import com.fortune.project.service.FileStorageService;
import com.fortune.project.service.ProductService;
import com.fortune.project.util.AuthUtil;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final ModelMapper modelMapper;
    private final CategoryRepository categoryRepository;
    private final FileStorageService fileStorageService;
    private final AuthUtil authUtil;

    public ProductServiceImpl(ProductRepository repo, ModelMapper modelMapper, CategoryRepository categoryRepository, FileStorageService fileStorageService, AuthUtil authUtil) {
        this.modelMapper = modelMapper;
        this.productRepository = repo;
        this.categoryRepository = categoryRepository;
        this.fileStorageService = fileStorageService;
        this.authUtil = authUtil;
    }

    @Override
    public ApiResponse<ProductResponse> createProduct(
            Long categoryId,
            ProductRequest request,
            MultipartFile imageFile) {

        CategoryEntity category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Category", "categoryId", categoryId
                ));

        boolean productExist = category.getProducts()
                .stream()
                .anyMatch(p -> p.getName().equalsIgnoreCase(request.getName()));

        if (productExist) {
            throw new ApiException(
                    "Product with name " + request.getName() + " already exists!",
                    "Product existed!"
            );
        }

        ProductEntity productToCreated = modelMapper.map(request, ProductEntity.class);

        productToCreated.setPrice(request.getPrice());
        productToCreated.setSpecialPrice(request.getSpecialPrice());
        productToCreated.setUser(authUtil.loggedInUser());

        if (productToCreated.getSpecialPrice() <= 0 && request.getDiscount() > 0) {
            double specialPrice = request.getPrice() - (request.getDiscount() * 0.01) * request.getPrice();
            productToCreated.setSpecialPrice(specialPrice);
        }

        productToCreated.setCategory(category);

        String imagePath;
        if (imageFile != null && !imageFile.isEmpty()) {
            try {
                imagePath = fileStorageService.uploadFile(imageFile);
            } catch (IOException e) {
                throw new ApiException("Cannot upload image: " + e.getMessage());
            }
        } else {
            imagePath = ProductConstant.DEFAULT_IMAGE;
        }
        productToCreated.setImage(imagePath);

        ProductEntity createdProduct = productRepository.save(productToCreated);

        ProductResponse response = modelMapper.map(createdProduct, ProductResponse.class);

        return new ApiResponse<>(
                "Product created successfully",
                response,
                LocalDateTime.now()
        );
    }


    @Override
    public ApiResponse<PagingResponse<ProductResponse>> getAllProducts(String category, String keyword, Pageable pageable) {
        Specification<ProductEntity> spec = (root, query, cb) -> cb.conjunction(); // Tạo spec mặc định TRUE

        if (keyword != null && !keyword.trim().isEmpty()) {
            spec = spec.and((root, query, cb) ->
                    cb.like(cb.lower(root.get("name")), "%" + keyword.toLowerCase() + "%"));
        }

        if (category != null && !category.trim().isEmpty()) {
            spec = spec.and((root, query, cb) ->
                    cb.equal(root.get("category").get("name"), category));
        }

        Page<ProductEntity> products = productRepository.findAll(spec, pageable);
        Page<ProductResponse> productResponses = products.map(p -> modelMapper.map(p, ProductResponse.class));

        return new ApiResponse<>("All products fetched successfully",
                new PagingResponse<>(productResponses), LocalDateTime.now());
    }


    @Override
    public ApiResponse<PagingResponse<ProductResponse>> getAllProductsByCategoryId(Long categoryId, Pageable pageable) {
        if (!categoryRepository.existsById(categoryId)) {
            throw new ResourceNotFoundException("Category", "categoryId", categoryId);
        }
        Page<ProductEntity> products = productRepository.findByCategory_id(categoryId, pageable);
        Page<ProductResponse> responses = products
                .map(p -> modelMapper.map(p, ProductResponse.class));
        return new ApiResponse<>("All products with categoryId = " + categoryId + " fetched successfully",
                new PagingResponse<>(responses),
                LocalDateTime.now());
    }

    @Override
    public ApiResponse<PagingResponse<ProductResponse>> getAllProductsByKeyword(String keyword, Pageable pageable) {
        Page<ProductEntity> products = productRepository.findByNameContainingIgnoreCase(keyword, pageable);
        Page<ProductResponse> responses = products
                .map(p -> modelMapper.map(p, ProductResponse.class));
        return new ApiResponse<>("All products with keyword = " + keyword + " fetched successfully",
                new PagingResponse<>(responses),
                LocalDateTime.now());
    }

    @Override
    public ApiResponse<ProductResponse> updateProductByProductId(Long productId, ProductRequest request) {
        ProductEntity productFound = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Product", "productId", productId
                ));
        productFound.setName(request.getName());
        productFound.setDescription(request.getDescription());
        productFound.setStock(request.getStock());
        productFound.setPrice(request.getPrice());
        productFound.setSpecialPrice(request.getPrice() - (request.getDiscount() * 0.01) * request.getPrice());
        ProductEntity updatedProduct = productRepository.save(productFound);
        ProductResponse responses = modelMapper.map(updatedProduct, ProductResponse.class);
        return new ApiResponse<>("Product with productId = " + productId + " updated successfully",
                responses,
                LocalDateTime.now());
    }

    @Override
    public ApiResponse<Void> deleteProductById(Long productId) {
        ProductEntity foundedProduct = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "productId", productId));
        productRepository.delete(foundedProduct);
        return new ApiResponse<>("Product with productId = " + productId + " deleted successfully",
                null,
                LocalDateTime.now());
    }

    @Override
    public ApiResponse<ProductResponse> updateProductImage(long productId, MultipartFile image) {
        ProductEntity product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "productId", productId));

        String imagePath;

        if (image != null && !image.isEmpty()) {
            // Có ảnh upload -> upload ảnh mới
            try {
                imagePath = fileStorageService.uploadFile(image);
            } catch (IOException e) {
                throw new ApiException("Không thể upload ảnh: " + e.getMessage());
            }
        } else {
            // Không có ảnh -> dùng ảnh mặc định
            imagePath = ProductConstant.DEFAULT_IMAGE;
        }

        product.setImage(imagePath);
        ProductEntity updated = productRepository.save(product);
        ProductResponse response = modelMapper.map(updated, ProductResponse.class);

        return new ApiResponse<>("Product image updated successfully", response, LocalDateTime.now());
    }


    @Override
    public ProductEntity findById(Long productId) {
        return productRepository.findById(productId).orElseThrow(() -> new ResourceNotFoundException("Product", "product id", productId));
    }

    @Override
    public List<OrderItemEntity> buildOrderItems(List<OrderItemRequest> itemRequests, OrderEntity order) {
        List<OrderItemEntity> orderItems = new ArrayList<>();

        for (OrderItemRequest request : itemRequests) {
            // Tìm sản phẩm
            ProductEntity product = productRepository.findById(request.getProductId())
                    .orElseThrow(() -> new ResourceNotFoundException("Product", "productId", request.getProductId()));

            // Validate số lượng
            if (request.getQuantity() <= 0) {
                throw new IllegalArgumentException("Số lượng phải > 0 cho sản phẩm: " + product.getName());
            }

            // Tạo OrderItemEntity
            OrderItemEntity orderItem = new OrderItemEntity();
            orderItem.setOrder(order);
            orderItem.setProduct(product);
            orderItem.setQuantity(request.getQuantity());
            orderItem.setUnitPrice(product.getPrice());
            orderItem.setTotalPrice(product.getPrice() * request.getQuantity());

            orderItems.add(orderItem);
        }

        return orderItems;
    }

    @Override
    public ApiResponse<PagingResponse<ProductResponse>> getAllSellerProducts(String category, String keyword, Pageable pageable) {
        UserEntity seller = authUtil.loggedInUser();
        Page<ProductEntity> products = productRepository.findAllByUser(seller, pageable);

        Page<ProductResponse> productResponses = products.map(p -> modelMapper.map(p, ProductResponse.class));
        return new ApiResponse<>("All seller's product fetched successfully",
                new PagingResponse<>(productResponses), LocalDateTime.now());
    }


}
