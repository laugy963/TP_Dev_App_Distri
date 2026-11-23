package fr.cesi.bookshop.catalog.book;

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
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookServiceTest {

    @Mock
    private BookRepository repository;

    @InjectMocks
    private BookService service;

    @Test
    void create_saves_the_book() {
        when(repository.save(any(Book.class))).thenAnswer(invocation -> {
            Book book = invocation.getArgument(0);
            book.setId(1L);
            return book;
        });

        Book created = service.create(new BookRequest("9780000000001", "Titre", "Auteur", new BigDecimal("10.00")));

        assertThat(created.getId()).isEqualTo(1L);
        assertThat(created.getTitle()).isEqualTo("Titre");
    }

    @Test
    void findById_unknown_throws_BookNotFoundException() {
        when(repository.findById(42L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.findById(42L)).isInstanceOf(BookNotFoundException.class);
    }

    @Test
    void update_changes_fields() {
        Book book = new Book("9780000000002", "Ancien", "Auteur", new BigDecimal("10.00"));
        when(repository.findById(2L)).thenReturn(Optional.of(book));

        service.update(2L, new BookRequest("9780000000002", "Nouveau", "Auteur", new BigDecimal("12.00")));

        assertThat(book.getTitle()).isEqualTo("Nouveau");
        assertThat(book.getPrice()).isEqualByComparingTo("12.00");
    }
}
