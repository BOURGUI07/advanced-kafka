package com.example.payment.application.repo;

import com.example.payment.application.entity.Customer;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerRepo extends ReactiveCrudRepository<Customer, Integer> {
}
