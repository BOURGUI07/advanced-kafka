package com.example.order.application.mapper;

import com.example.common.events.order.OrderStatus;
import com.example.order.application.entity.OrderInventory;
import com.example.order.application.entity.OrderPayment;
import com.example.order.application.entity.PurchaseOrder;
import com.example.order.common.dto.*;
import org.springframework.stereotype.Component;

@Component
public class OrderMapper {
    public PurchaseOrder toPurchaseOrder(OrderCreationRequest request) {
        return PurchaseOrder.builder()
                .status(OrderStatus.PENDING)
                .amount(request.quantity()* request.unitPrice())
                .customerId(request.customerId())
                .productId(request.productId())
                .unitPrice(request.unitPrice())
                .build();
    }

    public PurchaseOrderDTO toPurchaseOrderDTO(PurchaseOrder purchaseOrder) {
        return PurchaseOrderDTO.builder()
                .amount(purchaseOrder.getAmount())
                .customerId(purchaseOrder.getCustomerId())
                .productId(purchaseOrder.getProductId())
                .unitPrice(purchaseOrder.getUnitPrice())
                .deliveryDate(purchaseOrder.getDeliveryDate())
                .orderId(purchaseOrder.getOrderId())
                .status(purchaseOrder.getStatus())
                .build();
    }

    public OrderInventoryDTO toOrderInventoryDTO(OrderInventory entity) {
        return OrderInventoryDTO.builder()
                .orderId(entity.getOrderId())
                .inventoryId(entity.getInventoryId())
                .message(entity.getMessage())
                .status(entity.getStatus())
                .build();
    }

    public OrderInventory toOrderInventory(OrderInventoryDTO dto) {
        return OrderInventory.builder()
                .inventoryId(dto.inventoryId())
                .message(dto.message())
                .status(dto.status())
                .orderId(dto.orderId())
                .build();
    }

    public OrderPaymentDTO toOrderPaymentDTO(OrderPayment entity) {
        return OrderPaymentDTO.builder()
                .orderId(entity.getOrderId())
                .paymentId(entity.getPaymentId())
                .message(entity.getMessage())
                .status(entity.getStatus())
                .build();
    }

    public OrderPayment toOrderPayment(OrderPaymentDTO dto) {
        return OrderPayment.builder()
                .paymentId(dto.paymentId())
                .message(dto.message())
                .status(dto.status())
                .orderId(dto.orderId())
                .build();
    }

    public OrderDetails toOrderDetails(OrderPaymentDTO paymentDTO, OrderInventoryDTO inventoryDTO, PurchaseOrderDTO purchaseOrderDTO) {
        return OrderDetails.builder()
                .inventory(inventoryDTO)
                .order(purchaseOrderDTO)
                .payment(paymentDTO)
                .build();
    }
}
