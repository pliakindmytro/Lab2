package com.library.lab2.repository;

import com.library.lab2.entity.Loan;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.time.LocalDate;

public interface LoanRepository extends JpaRepository<Loan, Long> {
    long countByReader_IdAndReturnDateIsNull(Long readerId);

    boolean existsByBook_IdAndReturnDateIsNull(Long bookId);

    List<Loan> findByReader_IdAndReturnDateGreaterThanEqualAndReturnDateIsNotNull(
            Long readerId,
            LocalDate startDate
    );

    boolean existsByReader_IdAndReturnDateIsNullAndDueDateBefore(
            Long readerId,
            LocalDate date
    );

    List<Loan> findByReader_IdAndReturnDateIsNull(Long readerId);
}
