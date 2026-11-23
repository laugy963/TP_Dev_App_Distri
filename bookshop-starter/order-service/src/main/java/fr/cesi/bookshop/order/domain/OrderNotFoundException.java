package fr.cesi.bookshop.order.domain;

public class OrderNotFoundException extends RuntimeException {

    public OrderNotFoundException(Long id) {
        super("Commande introuvable : id=" + id);
    }
}
