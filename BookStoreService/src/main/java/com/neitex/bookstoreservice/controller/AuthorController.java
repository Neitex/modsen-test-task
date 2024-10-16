package com.neitex.bookstoreservice.controller;

import com.neitex.bookstoreservice.dto.AuthorRequestDTO;
import com.neitex.bookstoreservice.dto.AuthorResponseDTO;
import com.neitex.bookstoreservice.service.AuthorService;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
public class AuthorController {
  private final AuthorService authorService;

  @GetMapping("/authors")
  public List<AuthorResponseDTO> getAuthors() {
    return authorService.getAuthors();
  }

  @GetMapping("/author/{id}")
  public AuthorResponseDTO getAuthorById(@PathVariable("id") Long id) {
    return authorService.findAuthorById(id);
  }

  @PutMapping("/author")
  public AuthorResponseDTO createAuthor(AuthorRequestDTO author) {
    return authorService.createAuthor(author);
  }

  @PutMapping("/author/{id}")
  public AuthorResponseDTO updateAuthor(@PathVariable("id") Long id, AuthorRequestDTO author) {
    return authorService.updateAuthor(id, author);
  }

  @DeleteMapping("/author/{id}")
  public void deleteAuthor(@PathVariable("id") Long id) {
    authorService.deleteAuthor(id);
  }
}
