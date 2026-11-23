package fr.cesi.bookshop.order.web;

import fr.cesi.bookshop.order.OrderService;
import fr.cesi.bookshop.order.catalog.UnknownBookException;
import fr.cesi.bookshop.order.domain.Order;
import fr.cesi.bookshop.order.domain.OrderNotFoundException;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService service;

    public OrderController(OrderService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<Order> create(@Valid @RequestBody CreateOrderRequest request) {
        Order order = service.placeOrder(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(order.getId()).toUri();
        return ResponseEntity.created(location).body(order);
    }

    @GetMapping("/{id}")
    public Order findById(@PathVariable Long id) {
        return service.findById(id);
    }

    @GetMapping
    public List<Order> findAll() {
        return service.findAll();
    }

    @ExceptionHandler(OrderNotFoundException.class)
    public ProblemDetail notFound(OrderNotFoundException ex) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(UnknownBookException.class)
    public ProblemDetail unknownBook(UnknownBookException ex) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.UNPROCESSABLE_ENTITY, ex.getMessage());
    }

    /** Le catalogue est arrêté : la commande échoue (couplage temporel, cf. TP6 et TP10). */
    @ExceptionHandler(ResourceAccessException.class)
    public ProblemDetail catalogUnavailable(ResourceAccessException ex) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.SERVICE_UNAVAILABLE,
                "catalog-service injoignable : " + ex.getMessage());
    }
}
