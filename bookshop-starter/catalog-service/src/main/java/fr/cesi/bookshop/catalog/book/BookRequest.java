package fr.cesi.bookshop.catalog.book;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record BookRequest(
        @NotBlank @Size(max = 20) String isbn,
        @NotBlank String title,
        @NotBlank String author,
        @NotNull @DecimalMin("0.0") BigDecimal price) {
}
