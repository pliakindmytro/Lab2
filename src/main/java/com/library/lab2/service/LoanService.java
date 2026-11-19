package com.library.lab2.service;

import com.library.lab2.repository.BookRepository;
import com.library.lab2.repository.LoanRepository;
import com.library.lab2.repository.ReaderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.library.lab2.dto.CreateLoanRequest;
import com.library.lab2.entity.Book;
import com.library.lab2.entity.Loan;
import com.library.lab2.entity.Reader;
import com.library.lab2.service.ReaderService;

import java.util.List;
import java.time.LocalDate;

@Service
public class LoanService {

    private final BookRepository bookRepository;
    private final ReaderRepository readerRepository;
    private final LoanRepository loanRepository;

    private final ReaderService readerService;

    public LoanService(
            BookRepository bookRepository,
            ReaderRepository readerRepository,
            LoanRepository loanRepository,
            ReaderService readerService
    ) {
        this.bookRepository = bookRepository;
        this.readerRepository = readerRepository;
        this.loanRepository = loanRepository;
        this.readerService = readerService;
    }

    @Transactional
    public Loan createLoan(CreateLoanRequest request) {
        Reader reader = readerRepository.findById(request.getReaderId())
                .orElseThrow(() -> new RuntimeException("Reader not found"));

        Book book = bookRepository.findById(request.getBookId())
                .orElseThrow(() -> new RuntimeException("Book not found"));

        if (!book.isAvailable()) {
            throw new RuntimeException("Book is not available");
        }

        boolean isReliable = readerService.isReliable(reader.getId());

        long activeLoans = loanRepository.countByReader_IdAndReturnDateIsNull(reader.getId());

        int maxLoans = isReliable ? 5 : 2;

        if (activeLoans >= maxLoans) {
            throw new RuntimeException("Reader has reached the maximum number of active loans");
        }

        if (loanRepository.existsByBook_IdAndReturnDateIsNull(book.getId())) {
            throw new RuntimeException("Book is already issued");
        }

        LocalDate loanDate = LocalDate.now();
        LocalDate dueDate = loanDate.plusDays(14);

        Loan loan = new Loan(reader, book, loanDate, dueDate);

        book.setAvailable(false);
        bookRepository.save(book);

        return loanRepository.save(loan);
    }

    @Transactional
    public Loan returnLoan(Long loanId) {
        Loan loan = loanRepository.findById(loanId)
                .orElseThrow(() -> new RuntimeException("Loan not found"));

        if (loan.getReturnDate() != null) {
            throw new RuntimeException("Book has already been returned");
        }

        loan.setReturnDate(LocalDate.now());

        Book book = loan.getBook();
        book.setAvailable(true);

        bookRepository.save(book);

        return loanRepository.save(loan);
    }

    public List<Loan> getActiveLoans(Long readerId) {

        if (!readerRepository.existsById(readerId)) {
            throw new RuntimeException("Reader not found");
        }

        return loanRepository.findByReader_IdAndReturnDateIsNull(readerId);
    }
}
