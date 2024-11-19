package com.neitex.bookstoreservice.repository;

import com.neitex.bookstoreservice.entity.Book;
import java.util.List;
import java.util.Optional;
import lombok.NonNull;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookRepository extends CrudRepository<Book, Long> {

  Optional<Book> findBookByISBN(String ISBN);

  boolean existsByISBN(String ISBN);

  @NonNull
  List<Book> findAll();

  List<Book> findBooksByAuthorId(Long authorId);

  int countBooksByAuthorId(Long authorId);
}