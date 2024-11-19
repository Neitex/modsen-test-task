package com.neitex.library.controller;

import com.neitex.library.dto.BookLeaseUpdateRequestDTO;
import com.neitex.library.dto.BookLeaseUpdateResponseDTO;
import com.neitex.library.dto.BookUpdateType;
import com.neitex.library.service.BookLeaseService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/internal-books-lease")
@AllArgsConstructor
public class InternalAPIController {

  private final BookLeaseService bookLeaseService;

  @PostMapping("/updates")
  public BookLeaseUpdateResponseDTO updateBook(@RequestBody
  BookLeaseUpdateRequestDTO bookLeaseUpdateRequestDTO) {
    if (bookLeaseUpdateRequestDTO.getUpdateType() == BookUpdateType.DELETED) {
      bookLeaseService.deleteBookLease(bookLeaseUpdateRequestDTO.getBookId());
    } else {
      bookLeaseService.createBookLease(bookLeaseUpdateRequestDTO.getBookId());
    }
    return new BookLeaseUpdateResponseDTO(true);
  }
}
