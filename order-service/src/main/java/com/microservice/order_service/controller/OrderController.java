package com.microservice.order_service.controller;

import com.microservice.order_service.dto.OrderRequest;
import com.microservice.order_service.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/order")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<String> placeOrder(@RequestBody OrderRequest orderRequest) {
        try {
            orderService.placeOrder(orderRequest);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body("Order Placed Successfully");
        } catch (IllegalArgumentException e) {
            // Handle business logic errors (like out of stock)
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Oops! " + e.getMessage());
        } catch (Exception e) {
            // Handle circuit breaker and other errors
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                    .body("Oops! Something went wrong. Our inventory service is temporarily unavailable. Please try again later.");
        }
    }
}