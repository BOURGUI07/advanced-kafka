package com.example.inventory.common.service;

import com.example.inventory.common.dto.InventoryDTO;
import com.example.inventory.common.dto.InventoryProcessRequest;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface InventoryService {
    Mono<InventoryDTO> process(InventoryProcessRequest processRequest);
    Mono<InventoryDTO> restore(UUID orderId);
}
