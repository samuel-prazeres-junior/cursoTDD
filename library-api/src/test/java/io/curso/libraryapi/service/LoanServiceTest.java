package io.curso.libraryapi.service;

import io.curso.libraryapi.api.dto.LoanFilterDTO;
import io.curso.libraryapi.api.repository.Book;
import io.curso.libraryapi.service.imp.LoanServiceImpl;
import io.curso.libraryapi.exception.BusinessException;
import io.curso.libraryapi.api.repository.Loan;
import io.curso.libraryapi.api.repository.LoanRepository;
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

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
public class LoanServiceTest {

    @MockBean
    LoanRepository repository;

    @MockBean
    private LoanService service;

    @BeforeEach
    void setUp() {
        this.service = new LoanServiceImpl(repository);
    }

    @Test
    @DisplayName("Deve salvar um emprestimo")
    public void saveLoanTest() {

        Book book = Book.builder().id(1L).build();
        String customer = "fulano";
        Loan savingLoan = Loan.builder()
                .book(book)
                .customer(customer)
                .loanDate(LocalDate.now())
                .build();

        Loan savedLoan = Loan.builder()
                .id(1L)
                .loanDate(LocalDate.now())
                .customer(customer)
                .book(book)
                .build();

        Mockito.when(repository.save(savingLoan)).thenReturn(savedLoan);

        Loan loan = service.save(savingLoan);

        Assertions.assertEquals(savedLoan.getId(), loan.getId());
        Assertions.assertEquals(savedLoan.getBook().getId(), loan.getBook().getId());
        Assertions.assertEquals(savedLoan.getCustomer(), loan.getCustomer());
        Assertions.assertEquals(savedLoan.getLoanDate(), loan.getLoanDate());

    }


    @Test
    @DisplayName("Deve lançar erro de negocio salvar um emprestimo com livro ja emprestado")
    public void loanedBookSaveTest() {

        Book book = Book.builder().id(1L).build();
        String customer = "fulano";
        Loan savingLoan = Loan.builder()
                .book(book)
                .customer(customer)
                .loanDate(LocalDate.now())
                .build();

        Mockito.when(repository.existsByBookAndNotReturned(book)).thenReturn(true);

        Throwable exception = org.assertj.core.api.Assertions.catchThrowable(() -> service.save(savingLoan));

        org.assertj.core.api.Assertions.assertThat(exception)
                .isInstanceOf(BusinessException.class)
                .hasMessage("Book already loaned");

        Mockito.verify(repository, Mockito.never()).save(savingLoan);
    }
    @Test
    @DisplayName("Deve obter as ubfirnações de um emprestimo pelo id")
    public void getLoanDetaisTest(){
        Loan loan = createLoan();
        long id = 1L;
        loan.setId(id);
        Mockito.when(repository.findById(id)).thenReturn(Optional.of(loan));
        Optional<Loan> result = service.getById(id);
        Assertions.assertEquals(true, result.isPresent());
        Assertions.assertEquals(id, result.get().getId());
        Assertions.assertEquals(loan.getCustomer(), result.get().getCustomer());
        Assertions.assertEquals(loan.getBook(), result.get().getBook());
        Assertions.assertEquals(loan.getLoanDate(), result.get().getLoanDate());
        Mockito.verify(repository, Mockito.times(1)).findById(id);
    }
    @Test
    @DisplayName("Deve atualizar um emprestimo")
    public void updateLoan(){
        Loan loan = createLoan();
        loan.setId(1L);
        loan.setReturned(true);

        Mockito.when(repository.save(loan)).thenReturn(loan);

        Loan updatedLoan = service.update(loan);

        Assertions.assertEquals(true, updatedLoan.getReturned());
    }

    @Test
    @DisplayName("Deve filtrar emprestimos pelas propriedades")
    public void findLoanTest(){
        LoanFilterDTO filterDTO = LoanFilterDTO.builder().isbn("321").customer("Fulano").build();
        Loan loan = createLoan();
        loan.setId(1L);

        PageRequest pageRequest = PageRequest.of(0, 10);

        List<Loan> lista = Arrays.asList(loan);
        Page<Loan> page = new PageImpl<>(lista,pageRequest , lista.size());
        Mockito.when(repository.findByBookIsbnOrCustomer(Mockito.any(String.class),Mockito.any(String.class), Mockito.any(PageRequest.class))).thenReturn(page);

        Page<Loan> result = service.find(filterDTO, pageRequest);

        Assertions.assertEquals(1, result.getTotalElements());
        Assertions.assertEquals(lista, result.getContent());
        Assertions.assertEquals(0, result.getPageable().getPageNumber());
        Assertions.assertEquals(10, result.getPageable().getPageSize());


    }

    public static Loan createLoan(){
        Book book = Book.builder().id(1L).build();
        String customer = "fulano";

        return Loan.builder()
                .book(book)
                .customer(customer)
                .loanDate(LocalDate.now())
                .build();
    }
}
