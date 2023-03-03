package io.curso.libraryapi.service;

import io.curso.libraryapi.api.repository.Book;
import io.curso.libraryapi.api.repository.BookRepository;
import io.curso.libraryapi.service.imp.BookServiceImp;
import io.curso.libraryapi.exception.BusinessException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
public class BookServiceTest {

    BookService service;
    @MockBean
    BookRepository repository;

    @BeforeEach
    void setUp(){
        this.service = new BookServiceImp(repository);
    }

    @Test
    @DisplayName("Deve salvar um livro")
    public void saveBookTest(){
        Book book = createValidBook();
        Mockito.when(repository.existsByIsbn(Mockito.anyString())).thenReturn(false);
        Book booksave = Book.builder().id(11L).isbn("123").autor("Fulano").title("As aventuras").build();
        Mockito.when(repository.save(book)).thenReturn(booksave);

        Book save = service.save(book);

        assertThat(save.getId()).isNotNull();
        assertThat(save.getIsbn()).isEqualTo("123");
        assertThat(save.getTitle()).isEqualTo("As aventuras");
        assertThat(save.getAutor()).isEqualTo("Fulano");
    }

    private static Book createValidBook() {
        return Book.builder().isbn("123").autor("Fulano").title("As aventuras").build();
    }

    @Test
    @DisplayName("Deve lançar erro de negocio ao tentar salvar um livro com isbn duplicado")
    public void shouldNotSaveABooWithDuplicatedISBN(){
        Book book = createValidBook();
        Mockito.when(repository.existsByIsbn(Mockito.anyString())).thenReturn(true);
        Assertions.assertThrows(BusinessException.class, ()-> service.save(book)).getMessage().equalsIgnoreCase("isbn já cadastrado");
        Mockito.verify(repository, Mockito.never()).save(book);
    }

    @Test
    @DisplayName("Deve obter um livro por id")
    public void getByIdTest(){
        Long id = 1l;
        Book book = createValidBook();
        book.setId(id);
        Mockito.when(repository.findById(id)).thenReturn(Optional.of(book));

        Optional<Book> foundBook = service.getById(id);

        Assertions.assertEquals(true, foundBook.isPresent());
        Assertions.assertEquals(book.getAutor(), foundBook.get().getAutor());
        Assertions.assertEquals(book.getIsbn(), foundBook.get().getIsbn());
        Assertions.assertEquals(book.getTitle(), foundBook.get().getTitle());
    }

    @Test
    @DisplayName("Deve retornar vazio ao obter um livro por id quando ele não existir na base")
    public void bookNotFoundByIdTest(){
        Long id = 1l;
        Mockito.when(repository.findById(id)).thenReturn(Optional.empty());

        Optional<Book> book = service.getById(id);

        Assertions.assertEquals(false, book.isPresent());

    }

    @Test
    @DisplayName("Deve atualizar um livro")
    public void updateBookTest(){
        Long id = 1L;
        Book bookUpdating = Book.builder().id(id).build();
        Book bookUpdate = createValidBook();
        bookUpdate.setId(id);
//        Mockito.when(repository.existsById(Mockito.anyLong())).thenReturn(true);
        Mockito.when(repository.save(bookUpdating)).thenReturn(bookUpdate);
        Book update = service.update(bookUpdating);
        Assertions.assertEquals(update.getId(), bookUpdate.getId());
        Assertions.assertEquals(update.getTitle(), bookUpdate.getTitle());
        Assertions.assertEquals(update.getAutor(), bookUpdate.getAutor());
        Assertions.assertEquals(update.getIsbn(), bookUpdate.getIsbn());
    }

    @Test
    @DisplayName("Deve dar erro ao tentar atualizar um livro inexistente")
    public void updateInexistentBookTest(){
        Book book = new Book();
        Assertions.assertThrows(IllegalArgumentException.class, () -> service.update(book));
        Mockito.verify(repository, Mockito.never()).save(book);
    }

    @Test
    @DisplayName("Deve deletar um livro")
    public void deleteBookTest(){
        Book book = createValidBook();
        book.setId(1L);
        Assertions.assertDoesNotThrow(() -> service.delete(book));
        Mockito.verify(repository, Mockito.times(1)).delete(book);
    }

    @Test
    @DisplayName("Deve dar erro ao tentar atualizar um livro inexistente")
    public void deleteInexistentBookTest(){
        Book book = new Book();
        Assertions.assertThrows(IllegalArgumentException.class, () ->  service.delete(book));
        Mockito.verify(repository, Mockito.never()).save(book);
    }

    @Test
    @DisplayName("Deve filtrar livros pelas propriedades")
    public void findBookTest(){
        Book book = createValidBook();
        List<Book> lista = Arrays.asList(book);
        PageRequest pageRequest = PageRequest.of(0, 10);
        Page<Book> page = new PageImpl<>(lista,pageRequest , 1);
        Mockito.when(repository.findAll(Mockito.any(Example.class), Mockito.any(PageRequest.class))).thenReturn(page);

        Page<Book> result = service.find(book, pageRequest);

        Assertions.assertEquals(1, result.getTotalElements());
        Assertions.assertEquals(lista, result.getContent());
        Assertions.assertEquals(0, result.getPageable().getPageNumber());
        Assertions.assertEquals(10, result.getPageable().getPageSize());


    }
    @Test
    @DisplayName("Deve opber um livro pelo isbn")
    public void getBookByIsbnTest(){
        String isbn = "1230";
        Mockito.when(repository.findByIsbn(isbn)).thenReturn(Optional.of(Book.builder().id(1L).isbn(isbn).build()));
        Optional<Book> book = service.getBookByIsbn(isbn);


        Assertions.assertEquals(true,book.isPresent());
        Assertions.assertEquals(1L,book.get().getId());
        Assertions.assertEquals(isbn,book.get().getIsbn());

        Mockito.verify(repository, Mockito.times(1)).findByIsbn(isbn);
    }

}
