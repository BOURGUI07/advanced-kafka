package com.example.order.application.entity;

import com.example.common.events.payment.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.util.UUID;

@Builder
@AllArgsConstructor
@Data
@NoArgsConstructor
@Accessors(chain = true)
@Table(name="order_payment")
public class OrderPayment {
    @Id
    private Integer id;
    private UUID orderId;
    private UUID paymentId;
    private Boolean success;
    private String message;
    private PaymentStatus status;
}
