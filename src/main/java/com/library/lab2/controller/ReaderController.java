package com.library.lab2.controller;

import com.library.lab2.entity.Reader;
import com.library.lab2.service.ReaderService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import com.library.lab2.entity.Loan;
import com.library.lab2.service.LoanService;

@RestController
@RequestMapping("/api/readers")
public class ReaderController {

    private final ReaderService readerService;
    private final LoanService loanService;

    public ReaderController(ReaderService readerService, LoanService loanService) {
        this.readerService = readerService;
        this.loanService = loanService;
    }

    @PostMapping
    public Reader createReader(@RequestBody Reader reader) {
        return readerService.createReader(reader);
    }

    @GetMapping("/{readerId}/reliability")
    public boolean checkReliability(@PathVariable Long readerId) {
        return readerService.isReliable(readerId);
    }

    @GetMapping("/{readerId}/loans/active")
    public List<Loan> getActiveLoans(@PathVariable Long readerId) {
        return loanService.getActiveLoans(readerId);
    }
}
