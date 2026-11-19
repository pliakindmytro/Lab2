package com.library.lab2.service;

import com.library.lab2.entity.Reader;
import com.library.lab2.repository.ReaderRepository;
import org.springframework.stereotype.Service;

import com.library.lab2.repository.LoanRepository;
import com.library.lab2.entity.Loan;

import java.time.LocalDate;
import java.util.List;

@Service
public class ReaderService {
    private final ReaderRepository readerRepository;
    private final LoanRepository loanRepository;

    public ReaderService(ReaderRepository readerRepository, LoanRepository loanRepository) {
        this.readerRepository = readerRepository;
        this.loanRepository = loanRepository;
    }

    public Reader createReader(Reader reader) {
        if (readerRepository.existsByEmail(reader.getEmail())) {
            throw new RuntimeException("Reader with this email already exists");
        }

        return readerRepository.save(reader);
    }

    public boolean isReliable(Long readerId) {
        LocalDate startDate = LocalDate.now().minusDays(30);

        List<Loan> returnedLoans = loanRepository
                .findByReader_IdAndReturnDateGreaterThanEqualAndReturnDateIsNotNull(
                        readerId,
                        startDate
                );

        if (returnedLoans.size() < 3) {
            return false;
        }

        boolean allReturnedOnTime = returnedLoans.stream()
                .allMatch(loan -> !loan.getReturnDate().isAfter(loan.getDueDate()));

        if (!allReturnedOnTime) {
            return false;
        }

        boolean hasOverdueLoans = loanRepository
                .existsByReader_IdAndReturnDateIsNullAndDueDateBefore(
                        readerId,
                        LocalDate.now()
                );

        return !hasOverdueLoans;
    }
}