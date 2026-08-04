package com.ikonicit.invoice.service;


import com.ikonicit.invoice.dto.OrderRequestDTO;
import com.ikonicit.invoice.dto.OrderResponseDTO;
import com.ikonicit.invoice.dto.OrderStatusUpdateDTO;
import com.ikonicit.invoice.entity.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface OrderService {

    OrderResponseDTO createOrder(OrderRequestDTO request);

    OrderResponseDTO getOrderById(Long id);

    Page<OrderResponseDTO> getAllOrders(Pageable pageable);

    Page<OrderResponseDTO> getOrdersByStatus(OrderStatus status, Pageable pageable);

    List<OrderResponseDTO> getOrdersByClient(Long clientId);

    OrderResponseDTO updateOrder(Long id, OrderRequestDTO request);

    OrderResponseDTO updateOrderStatus(Long id, OrderStatusUpdateDTO request);

    void deleteOrder(Long id);
}
