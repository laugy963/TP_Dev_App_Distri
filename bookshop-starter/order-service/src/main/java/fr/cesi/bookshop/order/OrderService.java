package fr.cesi.bookshop.order;

import fr.cesi.bookshop.order.catalog.BookDto;
import fr.cesi.bookshop.order.catalog.CatalogClient;
import fr.cesi.bookshop.order.catalog.UnknownBookException;
import fr.cesi.bookshop.order.domain.Order;
import fr.cesi.bookshop.order.domain.OrderNotFoundException;
import fr.cesi.bookshop.order.domain.OrderRepository;
import fr.cesi.bookshop.order.web.CreateOrderRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class OrderService {

    private final OrderRepository orders;
    private final CatalogClient catalogClient;

    public OrderService(OrderRepository orders, CatalogClient catalogClient) {
        this.orders = orders;
        this.catalogClient = catalogClient;
    }

    @Transactional
    public Order placeOrder(CreateOrderRequest request) {
        BookDto book = catalogClient.findBook(request.bookId())
                .orElseThrow(() -> new UnknownBookException(request.bookId()));
        Order order = new Order(request.bookId(), request.quantity(), request.customerId());
        order.confirm(book.price());
        return orders.save(order);
    }

    @Transactional(readOnly = true)
    public Order findById(Long id) {
        return orders.findById(id).orElseThrow(() -> new OrderNotFoundException(id));
    }

    @Transactional(readOnly = true)
    public List<Order> findAll() {
        return orders.findAll();
    }
}
