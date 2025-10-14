package com.microservice.order_service.service;

import com.microservice.order_service.dto.InventoryResponse;
import com.microservice.order_service.dto.OrderRequest;
import com.microservice.order_service.dto.OrderLineItemsDto;
import com.microservice.order_service.model.Order;
import com.microservice.order_service.model.OrderLineItems;
import com.microservice.order_service.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.micrometer.tracing.Span;
import io.micrometer.tracing.Tracer;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderService {

    private final OrderRepository orderRepository;
    private final WebClient.Builder webClientBuilder;

    private final Tracer tracer;

    public void placeOrder(OrderRequest orderRequest) {
        Order order = new Order();
        order.setOrderNumber(UUID.randomUUID().toString());

        List<OrderLineItems> orderLineItems = orderRequest.getOrderLineItemsDtoList()
                .stream()
                .map(this::mapToDto)
                .toList();

        order.setOrderLineItemsList(orderLineItems);

        List<String> skuCodes = order.getOrderLineItemsList().stream()
                .map(OrderLineItems::getSkuCode)
                .toList();

        Span inventoryServiceLookup = tracer.nextSpan().name("InventoryServiceLookup");

        try (Tracer.SpanInScope spanInScope = tracer.withSpan(inventoryServiceLookup.start())) {
            // Call the circuit breaker protected method
            InventoryResponse[] inventoryResponseArray = callInventoryService(skuCodes);

            boolean allProductsInStock = Arrays.stream(inventoryResponseArray).allMatch(InventoryResponse::isInStock);

            if (allProductsInStock) {
                orderRepository.save(order);
            } else {
                throw new IllegalArgumentException("Product is not in stock , please try again");
            }
        } finally {
            inventoryServiceLookup.end();
        }
    }

    // Move the circuit breaker here - on the actual external service call
    @CircuitBreaker(name = "inventory", fallbackMethod = "fallbackMethod")
    public InventoryResponse[] callInventoryService(List<String> skuCodes) {
        return webClientBuilder.build().get()
                .uri("http://inventory-service/api/inventory", uriBuilder -> {
                    for (String skuCode : skuCodes) {
                        uriBuilder.queryParam("skuCode", skuCode);
                    }
                    return uriBuilder.build();
                })
                .retrieve()
                .bodyToMono(InventoryResponse[].class)
                .block();
    }

    // Fallback method for service-level circuit breaking
    public InventoryResponse[] fallbackMethod(List<String> skuCodes, RuntimeException runtimeException) {
        System.out.println(
                "Cannot get inventory for skuCodes " + skuCodes + ", failure reason: " + runtimeException.getMessage());
        // Return a default response indicating no items are in stock
        return skuCodes.stream()
                .map(skuCode -> {
                    InventoryResponse response = new InventoryResponse();
                    response.setSkuCode(skuCode);
                    response.setInStock(false);
                    return response;
                })
                .toArray(InventoryResponse[]::new);
    }

    private OrderLineItems mapToDto(OrderLineItemsDto orderLineItemsDto) {
        OrderLineItems orderLineItems = new OrderLineItems();
        orderLineItems.setPrice(orderLineItemsDto.getPrice());
        orderLineItems.setQuantity(orderLineItemsDto.getQuantity());
        orderLineItems.setSkuCode(orderLineItemsDto.getSkuCode());
        return orderLineItems;
    }
}