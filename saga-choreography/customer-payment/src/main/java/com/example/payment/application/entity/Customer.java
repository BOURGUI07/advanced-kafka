package com.example.payment.application.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.Id;

@NoArgsConstructor
@Data
@Accessors(chain = true)
public class Customer {
    @Id
    private Integer id;
    private String name;
    private Integer balance;
}
