package fr.cesi.bookshop.order.catalog;

public class UnknownBookException extends RuntimeException {

    public UnknownBookException(Long bookId) {
        super("Livre inconnu du catalogue : id=" + bookId);
    }
}
