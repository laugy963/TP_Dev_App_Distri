package fr.cesi.bookshop.catalog.book;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class BookService {

    private final BookRepository repository;

    public BookService(BookRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public List<Book> findAll() {
        return repository.findAll();
    }

    @Transactional(readOnly = true)
    public Book findById(Long id) {
        return repository.findById(id).orElseThrow(() -> new BookNotFoundException(id));
    }

    public Book create(BookRequest request) {
        return repository.save(new Book(request.isbn(), request.title(), request.author(), request.price()));
    }

    public Book update(Long id, BookRequest request) {
        Book book = findById(id);
        book.setIsbn(request.isbn());
        book.setTitle(request.title());
        book.setAuthor(request.author());
        book.setPrice(request.price());
        return book;
    }

    public void delete(Long id) {
        repository.delete(findById(id));
    }
}
