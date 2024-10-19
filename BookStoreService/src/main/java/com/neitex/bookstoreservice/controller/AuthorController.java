package com.neitex.bookstoreservice.controller;

import com.neitex.bookstoreservice.dto.AuthorRequestDTO;
import com.neitex.bookstoreservice.dto.AuthorResponseDTO;
import com.neitex.bookstoreservice.service.AuthorService;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
public class AuthorController {
  private final AuthorService authorService;

  @GetMapping("/authors") @PreAuthorize("hasRole('EDITOR') or hasRole('VIEWER')")
  public List<AuthorResponseDTO> getAuthors() {
    return authorService.getAuthors();
  }

  @GetMapping("/author/{id}") @PreAuthorize("hasRole('EDITOR') or hasRole('VIEWER')")
  public AuthorResponseDTO getAuthorById(@PathVariable("id") Long id) {
    return authorService.findAuthorById(id);
  }

  @PutMapping("/author") @PreAuthorize("hasRole('EDITOR')")
  public ResponseEntity<AuthorResponseDTO> createAuthor(@RequestBody AuthorRequestDTO author) {
    return ResponseEntity.status(HttpStatus.CREATED).body(authorService.createAuthor(author));
  }

  @PostMapping("/author/{id}") @PreAuthorize("hasRole('EDITOR')")
  public AuthorResponseDTO updateAuthor(@PathVariable("id") Long id, @RequestBody AuthorRequestDTO author) {
    return authorService.updateAuthor(id, author);
  }

  @DeleteMapping("/author/{id}") @PreAuthorize("hasRole('EDITOR')")
  public ResponseEntity<Void> deleteAuthor(@PathVariable("id") Long id) {
    authorService.deleteAuthor(id);
    return ResponseEntity.noContent().build();
  }
}
