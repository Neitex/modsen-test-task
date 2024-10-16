package com.neitex.bookstoreservice.controller;

import com.neitex.bookstoreservice.dto.BookRequestDTO;
import com.neitex.bookstoreservice.dto.BookResponseDTO;
import com.neitex.bookstoreservice.service.BookService;
import java.util.List;
import java.util.Optional;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
public class BookController {
  private final BookService bookService;

  @GetMapping("/books")
  public List<BookResponseDTO> getBooks() {
    return bookService.getBooks();
  }

  @GetMapping("/book/{id}")
  public Optional<BookResponseDTO> getBookById(@PathVariable("id") Long id) {
    return bookService.findBookById(id);
  }

  @GetMapping("/book/by-isbn/{isbn}")
  public Optional<BookResponseDTO> getBookByISBN(@PathVariable("isbn") String isbn) {
    return bookService.findBookByISBN(isbn);
  }

  @PutMapping("/book")
  public BookResponseDTO createBook(BookRequestDTO book) {
    return bookService.createBook(book);
  }

  @PostMapping("/book/{id}")
  public BookResponseDTO updateBook(Long id, BookRequestDTO book) {
    return bookService.updateBook(id,book);
  }

  @DeleteMapping("/book/{id}")
  public void deleteBook(@PathVariable("id") Long id) {
    bookService.deleteBook(id);
  }
}
