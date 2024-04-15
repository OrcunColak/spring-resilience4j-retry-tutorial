package com.colak.springresilience4jretrytutorial.conroller;

import com.colak.springresilience4jretrytutorial.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    // http://localhost:8080/orders
    @GetMapping
    public String getByOrderNumber() {
        return orderService.getOrderByPostCode();
    }
}
