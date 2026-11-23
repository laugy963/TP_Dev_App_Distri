package fr.cesi.bookshop.order.domain;

/** TP10 : ajout de STOCK_RESERVED et du cycle PENDING -> STOCK_RESERVED -> CONFIRMED / CANCELLED. */
public enum OrderStatus {
    PENDING,
    CONFIRMED,
    CANCELLED
}
