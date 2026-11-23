package fr.cesi.bookshop.catalog.book;

public class BookNotFoundException extends RuntimeException {

    public BookNotFoundException(Long id) {
        super("Livre introuvable : id=" + id);
    }
}
