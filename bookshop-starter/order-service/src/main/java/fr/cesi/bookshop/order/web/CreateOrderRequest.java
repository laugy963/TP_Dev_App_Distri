package fr.cesi.bookshop.order.web;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateOrderRequest(
        @NotNull Long bookId,
        @NotNull @Min(1) Integer quantity,
        @NotBlank String customerId) {
}
