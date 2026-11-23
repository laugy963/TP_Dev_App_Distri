package fr.cesi.bookshop.order;

import fr.cesi.bookshop.order.catalog.BookDto;
import fr.cesi.bookshop.order.catalog.CatalogClient;
import fr.cesi.bookshop.order.catalog.UnknownBookException;
import fr.cesi.bookshop.order.domain.Order;
import fr.cesi.bookshop.order.domain.OrderRepository;
import fr.cesi.bookshop.order.domain.OrderStatus;
import fr.cesi.bookshop.order.web.CreateOrderRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orders;

    @Mock
    private CatalogClient catalogClient;

    @InjectMocks
    private OrderService service;

    @Test
    void order_for_existing_book_is_confirmed_with_total() {
        when(catalogClient.findBook(1L)).thenReturn(Optional.of(
                new BookDto(1L, "9780132350884", "Clean Code", "Robert C. Martin", new BigDecimal("15.00"))));
        when(orders.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Order order = service.placeOrder(new CreateOrderRequest(1L, 2, "client-42"));

        assertThat(order.getStatus()).isEqualTo(OrderStatus.CONFIRMED);
        assertThat(order.getTotalAmount()).isEqualByComparingTo("30.00");
    }

    @Test
    void order_for_unknown_book_is_refused() {
        when(catalogClient.findBook(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.placeOrder(new CreateOrderRequest(99L, 1, "client-42")))
                .isInstanceOf(UnknownBookException.class);
        verify(orders, never()).save(any(Order.class));
    }
}
