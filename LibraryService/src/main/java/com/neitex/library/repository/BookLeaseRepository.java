package com.neitex.library.repository;

import com.neitex.library.model.BookLease;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface BookLeaseRepository extends JpaRepository<BookLease, Long> {

  @Query("from BookLease where leaseDate is null or returnDate is not null")
  List<BookLease> findAvailableBooks();
}
