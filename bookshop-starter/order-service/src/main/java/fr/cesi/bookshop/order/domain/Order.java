package fr.cesi.bookshop.order.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "book_id", nullable = false)
    private Long bookId;

    @Column(nullable = false)
    private Integer quantity;

    @Column(name = "customer_id", nullable = false)
    private String customerId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private OrderStatus status;

    @Column(name = "total_amount", precision = 10, scale = 2)
    private BigDecimal totalAmount;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    protected Order() {
    }

    public Order(Long bookId, Integer quantity, String customerId) {
        this.bookId = bookId;
        this.quantity = quantity;
        this.customerId = customerId;
        this.status = OrderStatus.PENDING;
    }

    @PrePersist
    void onCreate() {
        createdAt = Instant.now();
    }

    public void confirm(BigDecimal unitPrice) {
        this.totalAmount = unitPrice.multiply(BigDecimal.valueOf(quantity));
        this.status = OrderStatus.CONFIRMED;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getBookId() { return bookId; }
    public Integer getQuantity() { return quantity; }
    public String getCustomerId() { return customerId; }
    public OrderStatus getStatus() { return status; }
    public BigDecimal getTotalAmount() { return totalAmount; }
    public Instant getCreatedAt() { return createdAt; }
}
