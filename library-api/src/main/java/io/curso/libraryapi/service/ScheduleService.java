package io.curso.libraryapi.service;

import io.curso.libraryapi.api.repository.Loan;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ScheduleService {

    private static  final String CRON_LATE_LOANS = "0 0 0 1/1 * ?";

    private final LoanService loanService;
    private final EmailService emailService;

    @Value("${application.mail.lateloans.message}")
    private String mensagem;

    @Scheduled(cron = "0 35 12 1/1 * ?")
    public void sendMailToLateLoans(){
        List<Loan> allLateLoans = loanService.getAllLateLoans();
//        List<String> mailsList = allLateLoans.stream().map(loan -> loan.getCustomerEmail()).collect(Collectors.toList());
        List<String> mailsList = Arrays.asList("samuelprazeres@hotmail.com");

        emailService.sendMails(mensagem, mailsList);
        System.out.println("email enviado");
    }
}
