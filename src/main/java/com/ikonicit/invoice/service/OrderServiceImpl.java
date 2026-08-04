package com.ikonicit.invoice.service;


import com.ikonicit.invoice.dto.*;
import com.ikonicit.invoice.entity.Client;
import com.ikonicit.invoice.entity.Order;
import com.ikonicit.invoice.entity.OrderItem;
import com.ikonicit.invoice.entity.OrderStatus;
import com.ikonicit.invoice.exception.ResourceNotFoundException;
import com.ikonicit.invoice.repository.ClientRepository;
import com.ikonicit.invoice.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Year;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final ClientRepository clientRepository;

    private static final String ORDER_NUMBER_PREFIX_FORMAT = "ORD-%d-";

    @Override
    public OrderResponseDTO createOrder(OrderRequestDTO request) {
        Client client = clientRepository.findById(request.getClientId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Client not found with id: " + request.getClientId()));

        Order order = Order.builder()
                .orderNumber(generateNextOrderNumber())
                .client(client)
                .orderDate(request.getOrderDate())
                .expectedDeliveryDate(request.getExpectedDeliveryDate())
                .paymentMethod(request.getPaymentMethod())
                .status(request.getStatus() != null ? request.getStatus() : OrderStatus.NOT_STARTED)
                .notes(request.getNotes())
                .build();

        request.getItems().forEach(itemDto -> order.addItem(toItemEntity(itemDto)));
        recalculateTotals(order);

        Order saved = orderRepository.save(order);
        return toResponseDto(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public OrderResponseDTO getOrderById(Long id) {
        return toResponseDto(findOrderOrThrow(id));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<OrderResponseDTO> getAllOrders(Pageable pageable) {
        return orderRepository.findAll(pageable).map(this::toResponseDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<OrderResponseDTO> getOrdersByStatus(OrderStatus status, Pageable pageable) {
        return orderRepository.findByStatus(status, pageable).map(this::toResponseDto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderResponseDTO> getOrdersByClient(Long clientId) {
        return orderRepository.findByClientId(clientId).stream()
                .map(this::toResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public OrderResponseDTO updateOrder(Long id, OrderRequestDTO request) {
        Order order = findOrderOrThrow(id);

        if (!order.getClient().getId().equals(request.getClientId())) {
            Client client = clientRepository.findById(request.getClientId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Client not found with id: " + request.getClientId()));
            order.setClient(client);
        }

        order.setOrderDate(request.getOrderDate());
        order.setExpectedDeliveryDate(request.getExpectedDeliveryDate());
        order.setPaymentMethod(request.getPaymentMethod());
        order.setNotes(request.getNotes());
        if (request.getStatus() != null) {
            order.setStatus(request.getStatus());
        }

        order.clearItems();
        request.getItems().forEach(itemDto -> order.addItem(toItemEntity(itemDto)));
        recalculateTotals(order);

        Order saved = orderRepository.save(order);
        return toResponseDto(saved);
    }

    @Override
    public OrderResponseDTO updateOrderStatus(Long id, OrderStatusUpdateDTO request) {
        Order order = findOrderOrThrow(id);
        order.setStatus(request.getStatus());
        Order saved = orderRepository.save(order);
        return toResponseDto(saved);
    }

    @Override
    public void deleteOrder(Long id) {
        Order order = findOrderOrThrow(id);
        orderRepository.delete(order);
    }

    // ---------- helpers ----------

    private Order findOrderOrThrow(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + id));
    }

    /**
     * Generates ORD-{year}-{sequence}, zero-padded to 4 digits, scoped
     * to the current calendar year — same approach as your Invoice
     * module's INV-{year}-{sequence}. Swap out for your exact
     * implementation if it differs (e.g. padding width, reset rule).
     */
    private String generateNextOrderNumber() {
        int year = Year.now().getValue();
        String prefix = String.format(ORDER_NUMBER_PREFIX_FORMAT, year);
        long countThisYear = orderRepository.countByOrderNumberStartingWith(prefix);
        long nextSequence = countThisYear + 1;
        return prefix + String.format("%04d", nextSequence);
    }

    private OrderItem toItemEntity(OrderItemRequestDTO dto) {
        return OrderItem.builder()
                .productId(dto.getProductId())
                .description(dto.getDescription())
                .quantity(dto.getQuantity())
                .unit(dto.getUnit())
                .unitPrice(dto.getUnitPrice())
                .taxPercent(dto.getTaxPercent())
                .deliveredQuantity(BigDecimal.ZERO)
                .build();
    }

    private void recalculateTotals(Order order) {
        BigDecimal subtotal = order.getItems().stream()
                .map(OrderItem::lineSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal taxAmount = order.getItems().stream()
                .map(OrderItem::lineTax)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        order.setSubtotal(subtotal);
        order.setTaxAmount(taxAmount);
        order.setTotalAmount(subtotal.add(taxAmount));
    }

    private OrderResponseDTO toResponseDto(Order order) {
        List<OrderItemResponseDTO> itemDtos = order.getItems().stream()
                .map(item -> OrderItemResponseDTO.builder()
                        .id(item.getId())
                        .productId(item.getProductId())
                        .description(item.getDescription())
                        .quantity(item.getQuantity())
                        .unit(item.getUnit())
                        .unitPrice(item.getUnitPrice())
                        .taxPercent(item.getTaxPercent())
                        .deliveredQuantity(item.getDeliveredQuantity())
                        .lineSubtotal(item.lineSubtotal())
                        .lineTax(item.lineTax())
                        .lineTotal(item.lineTotal())
                        .build())
                .collect(Collectors.toList());

        Client client = order.getClient();
        String clientName;
        if ("COMPANY".equalsIgnoreCase(client.getClientType())
                && client.getCompany() != null
                && !client.getCompany().isBlank()) {
            clientName = client.getCompany();
        } else {
            String fn = client.getFirstName() != null ? client.getFirstName() : "";
            String ln = client.getLastName() != null ? client.getLastName() : "";
            clientName = (fn + " " + ln).trim();
            if (clientName.isEmpty()) {
                clientName = client.getCompany(); // fallback, may be null
            }
        }

        return OrderResponseDTO.builder()
                .id(order.getId())
                .orderNumber(order.getOrderNumber())
                .clientId(client.getId())
                .clientName(clientName)
                .orderDate(order.getOrderDate())
                .expectedDeliveryDate(order.getExpectedDeliveryDate())
                .paymentMethod(order.getPaymentMethod())
                .status(order.getStatus())
                .notes(order.getNotes())
                .items(itemDtos)
                .subtotal(order.getSubtotal())
                .taxAmount(order.getTaxAmount())
                .totalAmount(order.getTotalAmount())
                .createdAt(order.getCreatedAt())
                .updatedAt(order.getUpdatedAt())
                .build();
    }
}
