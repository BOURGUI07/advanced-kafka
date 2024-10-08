package com.example.order.common.dto;

import lombok.Builder;

@Builder
public record OrderDetails(
        PurchaseOrderDTO order,
        OrderInventoryDTO inventory,
        OrderPaymentDTO payment
) {
}
