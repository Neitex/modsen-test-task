package com.neitex.bookstoreservice.controller;

import com.neitex.bookstoreservice.dto.BookRequestDTO;
import com.neitex.bookstoreservice.dto.BookResponseDTO;
import com.neitex.bookstoreservice.service.BookService;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
public class BookController {

  private final BookService bookService;

  @PreAuthorize("hasRole('EDITOR') or hasRole('VIEWER')")
  @GetMapping("/books")
  public List<BookResponseDTO> getBooks() {
    return bookService.getBooks();
  }

  @PreAuthorize("hasRole('EDITOR') or hasRole('VIEWER')")
  @GetMapping("/books/{id}")
  public BookResponseDTO getBookById(@PathVariable("id") Long id) {
    return bookService.getBookByID(id);
  }

  @PreAuthorize("hasRole('EDITOR') or hasRole('VIEWER')")
  @GetMapping("/books/by-isbn/{isbn}")
  public BookResponseDTO getBookByIsbn(@PathVariable("isbn") String isbn) {
    return bookService.findBookByIsbn(isbn);
  }

  @PreAuthorize("hasRole('EDITOR') or hasRole('VIEWER')")
  @GetMapping("/books/by-author/{authorId}")
  public List<BookResponseDTO> getBooksByAuthor(@PathVariable("authorId") Long authorId) {
    return bookService.findBooksByAuthor(authorId);
  }

  @PreAuthorize("hasRole('EDITOR')")
  @PostMapping("/books")
  public ResponseEntity<BookResponseDTO> createBook(@RequestBody BookRequestDTO book) {
    return ResponseEntity.status(HttpStatus.CREATED).body(bookService.createBook(book));
  }

  @PreAuthorize("hasRole('EDITOR')")
  @PatchMapping("/books/{id}")
  public BookResponseDTO updateBook(@PathVariable("id") Long id, @RequestBody BookRequestDTO book) {
    return bookService.updateBook(id, book);
  }

  @PreAuthorize("hasRole('EDITOR')")
  @DeleteMapping("/books/{id}")
  public ResponseEntity<Void> deleteBook(@PathVariable("id") Long id) {
    bookService.deleteBook(id);
    return ResponseEntity.noContent().build();
  }
}
