package com.neitex.library.controller;

import com.neitex.library.dto.BookLeaseResponseDTO;
import com.neitex.library.dto.BookLeaseUpdateResponseDTO;
import com.neitex.library.model.BookUpdateType;
import com.neitex.library.service.BookLeaseService;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController("/books-lease")
@AllArgsConstructor
public class BookLeaseController {
  private final BookLeaseService bookLeaseService;

  @PostMapping("/updates")
  public BookLeaseUpdateResponseDTO updateBook(Long bookId, BookUpdateType updateType) {
    if (updateType == BookUpdateType.DELETED) {
      bookLeaseService.deleteBookLease(bookId);
    } else {
      bookLeaseService.createBookLease(bookId);
    }
    return new BookLeaseUpdateResponseDTO(true);
  }

  @PreAuthorize("hasRole('EDITOR') or hasRole('VIEWER')")
  @GetMapping("/leases")
  public List<BookLeaseResponseDTO> getBookLease() {
    return bookLeaseService.getBookLeases();
  }

  @PreAuthorize("hasRole('EDITOR') or hasRole('VIEWER')")
  @GetMapping("/lease/{bookId}")
  public BookLeaseResponseDTO getBookLease(@PathVariable("bookId") Long bookId) {
    return bookLeaseService.getBookLease(bookId);
  }

  @PreAuthorize("hasRole('EDITOR')")
  @PostMapping("/lease/{bookId}")
  public BookLeaseResponseDTO updateBookLease(@PathVariable("bookId") Long bookId, BookLeaseResponseDTO bookLeaseResponseDTO) {
    return bookLeaseService.updateBookLease(bookId, bookLeaseResponseDTO);
  }

  @PreAuthorize("hasRole('EDITOR')")
  @DeleteMapping("/lease/{bookId}")
  public void deleteBookLease(@PathVariable("bookId") Long bookId) {
    bookLeaseService.deleteBookLease(bookId);
  }

  @PreAuthorize("hasRole('EDITOR')")
  @PutMapping("/lease")
  public BookLeaseResponseDTO createBookLease(Long bookId) {
    return bookLeaseService.createBookLease(bookId);
  }
}
