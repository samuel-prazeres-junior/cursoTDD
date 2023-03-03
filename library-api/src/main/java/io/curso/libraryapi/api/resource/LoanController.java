package io.curso.libraryapi.api.resource;

import io.curso.libraryapi.api.dto.BookDTO;
import io.curso.libraryapi.api.dto.LoanDto;
import io.curso.libraryapi.api.dto.LoanFilterDTO;
import io.curso.libraryapi.api.dto.ReturnedDTO;
import io.curso.libraryapi.api.repository.Book;
import io.curso.libraryapi.api.repository.Loan;
import io.curso.libraryapi.service.BookService;
import io.curso.libraryapi.service.LoanService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/loans")
@RequiredArgsConstructor
public class LoanController {

    private final LoanService service;
    private final BookService bookService;
    private final ModelMapper modelMapper;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Long create(@RequestBody LoanDto dto){
        Book book = bookService.getBookByIsbn(dto.getIsbn()).orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST ,"Book not found for passed isbn"));
        Loan entity = Loan.builder().book(book).customerEmail(dto.getEmail()).customer(dto.getCustomer()).loanDate(LocalDate.now()).build();

        entity = service.save(entity);
        return entity.getId();
    }

    @PatchMapping("/{id}")
    public void returnBook(@PathVariable Long id, @RequestBody ReturnedDTO dto){
        Loan loan = service.getById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        loan.setReturned(dto.getReturned());
        service.update(loan);
    }

    @GetMapping
    public Page<LoanDto> find(LoanFilterDTO dto, Pageable pageRequest){
        Page<Loan> result = service.find(dto, pageRequest);
        List<LoanDto> loans = result.getContent().stream().map(entity -> {
            BookDTO bookDto = modelMapper.map(entity.getBook(), BookDTO.class);
            LoanDto loanDto = modelMapper.map(entity, LoanDto.class);
            loanDto.setBook(bookDto);
            return loanDto;
        }).collect(Collectors.toList());
        return new PageImpl<LoanDto>(loans, pageRequest, result.getTotalElements());

    }
}
