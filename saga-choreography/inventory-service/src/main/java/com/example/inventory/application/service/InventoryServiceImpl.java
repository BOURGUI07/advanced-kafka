package com.example.inventory.application.service;

import com.example.common.events.inventory.InventoryStatus;
import com.example.common.events.payment.PaymentStatus;
import com.example.common.util.DuplicateEventValidator;
import com.example.inventory.application.entity.OrderInventory;
import com.example.inventory.application.entity.Product;
import com.example.inventory.application.mapper.InventoryMapper;
import com.example.inventory.application.repo.OrderInventoryRepo;
import com.example.inventory.application.repo.ProductRepo;
import com.example.inventory.common.dto.InventoryDTO;
import com.example.inventory.common.dto.InventoryProcessRequest;
import com.example.inventory.common.exception.OutOfStockException;
import com.example.inventory.common.exception.ProductNotFoundException;
import com.example.inventory.common.service.InventoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

import java.util.UUID;
@Service
@RequiredArgsConstructor
@Slf4j
public class InventoryServiceImpl implements InventoryService {
    private final ProductRepo productRepo;
    private final OrderInventoryRepo orderInventoryRepo;
    private InventoryMapper mapper;


    @Override
    @Transactional
    public Mono<InventoryDTO> process(InventoryProcessRequest request) {
        var productId = request.productId();
        return DuplicateEventValidator.validate(
                        orderInventoryRepo.existsByOrderId(request.orderId()),
                        productRepo.findById(productId)
                )
                .switchIfEmpty(Mono.error(new ProductNotFoundException(productId)))
                .filter(p->p.getAvailableQuantity()>= request.quantity())
                .switchIfEmpty(Mono.error(new OutOfStockException(productId)))
                .flatMap(c -> deduct(c,request))
                .doOnNext(x-> log.info("ORDER INVENTORY PROCESSED FOR ORDER: {}", request.orderId()));

    }

    private Mono<InventoryDTO> deduct(Product product, InventoryProcessRequest request) {
        product.setAvailableQuantity(product.getAvailableQuantity()-request.quantity());
        var orderInventory = mapper.toEntity(request).setStatus(InventoryStatus.DEDUCTED);
        return productRepo.save(product)
                .then(orderInventoryRepo.save(orderInventory))
                .map(mapper::toDto);
    }

    @Override
    @Transactional
    public Mono<InventoryDTO> restore(UUID orderId) {
        return orderInventoryRepo.findByOrderIdAndStatus(orderId,InventoryStatus.DEDUCTED)
                .zipWhen(p->productRepo.findById(p.getProductId()))
                .flatMap(x->processRefund(x.getT1(),x.getT2()))
                .doOnNext(x->log.info("INVENTORY RESTORATION PROCESSED FOR ORDER: {} WITH QUANTITY: {}", orderId,x.quantity()));
    }

    private Mono<InventoryDTO> processRefund(OrderInventory p, Product c) {
        c.setAvailableQuantity(c.getAvailableQuantity()+p.getQuantity());
        p.setStatus(InventoryStatus.RESTORED);
        return productRepo.save(c)
                .then(orderInventoryRepo.save(p))
                .map(mapper::toDto);
    }
}
