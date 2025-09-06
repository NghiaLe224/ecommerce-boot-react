package com.fortune.project.service.impl;

import com.fortune.project.service.AnalyticsService;
import com.fortune.project.dto.response.analytics.AnalyticsResponse;
import com.fortune.project.repository.OrderRepository;
import com.fortune.project.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class AnalyticsServiceImpl implements AnalyticsService {

    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;

    @Override
    public AnalyticsResponse getAnalytics() {
        AnalyticsResponse res = new AnalyticsResponse();
        Long productsCount = productRepository.count();
        Long ordersCount = orderRepository.count();
        Double totalRevenue = orderRepository.getTotalRevenue();

        res.setProductCount(productsCount);
        res.setTotalOrders(ordersCount);
        res.setTotalRevenue(totalRevenue);

        return res;
    }
}
