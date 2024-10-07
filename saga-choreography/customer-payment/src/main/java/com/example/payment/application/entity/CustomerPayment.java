package com.example.payment.application.entity;

import com.example.common.events.payment.PaymentStatus;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.util.UUID;

@Table(name="customer_payment")
@NoArgsConstructor
@Data
@Accessors(chain = true)
public class CustomerPayment {
    @Id
    private UUID paymentId;
    private UUID orderId;
    private Integer customerId;
    private PaymentStatus status;
    private Integer amount;
}
