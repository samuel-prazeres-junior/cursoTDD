package io.curso.libraryapi.api.exceptions;

import io.curso.libraryapi.exception.BusinessException;
import lombok.Getter;
import org.springframework.validation.BindingResult;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Getter
public class ApiErrors {
    private List<String> errors;
    public ApiErrors(BindingResult bindingResult) {
        this.errors = new ArrayList();
        bindingResult.getAllErrors().forEach(error -> errors.add(error.getDefaultMessage()));
    }

    public ApiErrors(BusinessException ex) {
        this.errors = Arrays.asList(ex.getMessage());
    }

    public ApiErrors(ResponseStatusException ex) {
        this.errors = Arrays.asList(ex.getMessage());
    }
}
