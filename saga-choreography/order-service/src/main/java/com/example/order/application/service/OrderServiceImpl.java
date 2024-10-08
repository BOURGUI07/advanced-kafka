package com.example.order.application.service;

import com.example.order.application.mapper.OrderMapper;
import com.example.order.application.repo.OrderInventoryRepo;
import com.example.order.application.repo.OrderPaymentRepo;
import com.example.order.application.repo.PurchaseOrderRepo;
import com.example.order.common.dto.OrderCreationRequest;
import com.example.order.common.dto.OrderDetails;
import com.example.order.common.dto.PurchaseOrderDTO;
import com.example.order.common.service.OrderEventListener;
import com.example.order.common.service.OrderService;
import com.example.order.common.service.inventory.InventoryComponentFetcher;
import com.example.order.common.service.payment.PaymentComponentFetcher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderServiceImpl implements OrderService {
    private final OrderMapper orderMapper;
    private final PurchaseOrderRepo purchaseOrderRepo;
    private final OrderEventListener orderEventListener;
    private final InventoryComponentFetcher inventoryComponentFetcher;
    private final PaymentComponentFetcher paymentComponentFetcher;


    @Override
    public Mono<PurchaseOrderDTO> placeOrder(OrderCreationRequest request) {
        return purchaseOrderRepo.save(orderMapper.toPurchaseOrder(request))
                .map(orderMapper::toPurchaseOrderDTO)
                .doOnNext(orderEventListener::emitCreatedOrder);
    }

    @Override
    public Flux<PurchaseOrderDTO> getAllOrders() {
        return purchaseOrderRepo.findAll()
                .map(orderMapper::toPurchaseOrderDTO);
    }

    @Override
    public Mono<OrderDetails> getOrderDetails(UUID orderId) {
        return purchaseOrderRepo.findById(orderId)
                .map(orderMapper::toPurchaseOrderDTO)
                .flatMap(dto ->inventoryComponentFetcher.getComponent(orderId)
                        .zipWith(paymentComponentFetcher.getComponent(orderId))
                        .map(x->orderMapper.toOrderDetails(x.getT2(),x.getT1(),dto)));
    }
}
