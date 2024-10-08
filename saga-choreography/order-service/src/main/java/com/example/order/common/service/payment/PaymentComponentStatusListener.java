package com.example.order.common.service.payment;

import com.example.order.common.dto.OrderInventoryDTO;
import com.example.order.common.dto.OrderPaymentDTO;
import com.example.order.common.service.OrderComponentStatusListener;

public interface PaymentComponentStatusListener extends OrderComponentStatusListener<OrderPaymentDTO> {
}
