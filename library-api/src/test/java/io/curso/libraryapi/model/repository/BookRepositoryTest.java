package io.curso.libraryapi.model.repository;


import io.curso.libraryapi.api.repository.Book;
import io.curso.libraryapi.api.repository.BookRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@DataJpaTest
public class BookRepositoryTest {

    @Autowired
    TestEntityManager entityManager;

    @Autowired
    BookRepository repository;

    @Test
    @DisplayName("Deve retornar verdadeiro quando existir um livro com isbn informado")
    public void returnTrueWhenIsbnExistis(){
        String isbn = "123";

        Book book = Book.builder().title("As aventuras").autor("Fulano").isbn("123").build();

        entityManager.persist(book);

        boolean exists = repository.existsByIsbn(isbn);

        Assertions.assertEquals(exists, true);
    }

    @Test
    @DisplayName("Deve retornar false quando não existir um livro com isbn informado")
    public void returnFalseWhenIsbnExistis(){
        String isbn = "123";

        boolean exists = repository.existsByIsbn(isbn);

        Assertions.assertEquals(exists, false);
    }


    @Test
    @DisplayName("Deve obter um livro por id")
    public void findByIdTest(){
        Book book = createNewBook("123");
        entityManager.persist(book);

        Optional<Book> foundBook = repository.findById(book.getId());
        Assertions.assertEquals(true, foundBook.isPresent());
    }

    public static Book createNewBook(String isbn) {
        return Book.builder().isbn(isbn).autor("Fulano").title("As aventuras").build();
    }

    @Test
    @DisplayName("Deve salvar um livro")
    public void saveBookTest(){
        Book book = createNewBook("123");
        Book savedBook = repository.save(book);

        org.assertj.core.api.Assertions.assertThat(savedBook.getId()).isNotNull();
    }

    @Test
    @DisplayName("Deve deletar um livro")
    public void deleteBookTest(){
        Book book = createNewBook("123");
        entityManager.persist(book);

        Book foundBook = entityManager.find(Book.class, book.getId());

        repository.delete(foundBook);

        Book deleteBook = entityManager.find(Book.class, book.getId());

        org.assertj.core.api.Assertions.assertThat(deleteBook).isNull();
    }
}
