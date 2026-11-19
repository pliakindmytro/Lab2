package com.library.lab2.controller;

import com.library.lab2.dto.CreateLoanRequest;
import com.library.lab2.entity.Loan;
import com.library.lab2.service.LoanService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/loans")
public class LoanController {

    private final LoanService loanService;

    public LoanController(LoanService loanService) {
        this.loanService = loanService;
    }

    @PostMapping
    public Loan createLoan(@RequestBody CreateLoanRequest request) {
        return loanService.createLoan(request);
    }

    @PostMapping("/{loanId}/return")
    public Loan returnLoan(@PathVariable Long loanId) {
        return loanService.returnLoan(loanId);
    }
}
