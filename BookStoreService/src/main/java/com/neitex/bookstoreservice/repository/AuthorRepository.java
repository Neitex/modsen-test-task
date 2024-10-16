package com.neitex.bookstoreservice.repository;

import com.neitex.bookstoreservice.entity.Author;
import java.util.List;
import lombok.NonNull;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuthorRepository extends CrudRepository<Author, Long> {
  @NonNull
  List<Author> findAll();

  boolean existsByName(String name);

}
