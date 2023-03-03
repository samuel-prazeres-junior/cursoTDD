package io.curso.libraryapi.service;

import org.springframework.stereotype.Service;

import java.util.List;

public interface EmailService {
    void sendMails(String message, List<String> mailsList) ;
}
