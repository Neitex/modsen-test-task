package com.neitex.library.repository;

import com.neitex.library.model.BookLease;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookLeaseRepository extends JpaRepository<BookLease, Long> {

}
