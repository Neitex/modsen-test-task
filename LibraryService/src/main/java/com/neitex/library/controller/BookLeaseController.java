package com.neitex.library.controller;

import com.neitex.library.dto.BookLeaseRequestDTO;
import com.neitex.library.dto.BookLeaseResponseDTO;
import com.neitex.library.service.BookLeaseService;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/books-lease")
@AllArgsConstructor
public class BookLeaseController {

  private final BookLeaseService bookLeaseService;

  @PreAuthorize("hasRole('EDITOR') or hasRole('VIEWER')")
  @GetMapping("/leases")
  public List<BookLeaseResponseDTO> getBookLease() {
    return bookLeaseService.getBookLeases();
  }

  @PreAuthorize("hasRole('EDITOR') or hasRole('VIEWER')")
  @GetMapping("/leases/{bookId}")
  public BookLeaseResponseDTO getBookLease(@PathVariable("bookId") Long bookId) {
    return bookLeaseService.getBookLease(bookId);
  }

  @PreAuthorize("hasRole('EDITOR')")
  @PostMapping("/leases/{bookId}/lease")
  public BookLeaseResponseDTO leaseBook(@PathVariable("bookId") Long bookId,
      @RequestBody BookLeaseRequestDTO bookLeaseRequestDTO) {
    return bookLeaseService.leaseBook(bookId, bookLeaseRequestDTO);
  }

  @PreAuthorize("hasRole('EDITOR')")
  @PostMapping("/leases/{bookId}/return")
  public BookLeaseResponseDTO returnBook(@PathVariable("bookId") Long bookId) {
    return bookLeaseService.returnBook(bookId);
  }

  @PreAuthorize("hasRole('VIEWER') or hasRole('EDITOR')")
  @GetMapping("/leases/available")
  public List<BookLeaseResponseDTO> getAvailableBooks() {
    return bookLeaseService.getAvailableBooks();
  }
}
