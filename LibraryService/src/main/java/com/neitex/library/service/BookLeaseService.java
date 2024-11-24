package com.neitex.library.service;

import com.neitex.library.dto.BookLeaseRequestDTO;
import com.neitex.library.dto.BookLeaseResponseDTO;
import com.neitex.library.exception.BadFieldContentsException;
import com.neitex.library.exception.BookLeaseAlreadyExistsException;
import com.neitex.library.exception.BookLeaseDoesNotExist;
import com.neitex.library.exception.IllegalLeaseStateException;
import com.neitex.library.model.BookLease;
import com.neitex.library.repository.BookLeaseRepository;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class BookLeaseService {

  private final BookLeaseRepository bookLeaseRepository;
  private final ModelMapper modelMapper;

  public BookLeaseResponseDTO getBookLease(Long bookId) {
    return modelMapper.map(bookLeaseRepository.findById(bookId)
            .orElseThrow(
                () -> new BookLeaseDoesNotExist("Book lease with ID " + bookId + " does not exist")),
        BookLeaseResponseDTO.class);
  }

  public void createBookLease(Long bookId) {
    if (bookLeaseRepository.existsById(bookId)) {
      throw new BookLeaseAlreadyExistsException("Book lease with ID " + bookId + " already exists");
    }
    BookLease bookLease = new BookLease();
    bookLease.setBookId(bookId);
    bookLeaseRepository.save(bookLease);
  }

  public void deleteBookLease(Long bookId) {
    bookLeaseRepository.deleteById(bookId);
  }

  public BookLeaseResponseDTO leaseBook(Long bookId, BookLeaseRequestDTO bookLeaseRequestDTO) {
    BookLease bookLease = bookLeaseRepository.findById(bookId).orElseThrow(
        () -> new BookLeaseDoesNotExist("Book lease with ID " + bookId + " does not exist"));
    if (bookLease.getLeaseDate() != null) {
      throw new IllegalLeaseStateException(
          String.format("Book with ID %s is already leased (lease date: %s, return date: %s)",
              bookId, bookLease.getLeaseDate(), bookLease.getReturnDate().toString()));
    }
    if (bookLeaseRequestDTO.getLeaseDate() == null) {
      throw new BadFieldContentsException("Lease date is not set");
    }
    if (bookLeaseRequestDTO.getReturnDate() == null) {
      throw new BadFieldContentsException("Return date is not set");
    } else {
      if (!bookLeaseRequestDTO.getReturnDate().isAfter(bookLeaseRequestDTO.getLeaseDate())) {
        throw new BadFieldContentsException(
            String.format("Return date (%s) is before or equal to lease date (%s)",
                bookLeaseRequestDTO.getReturnDate(), bookLeaseRequestDTO.getLeaseDate()));
      }
    }
    bookLease.setLeaseDate(bookLeaseRequestDTO.getLeaseDate());
    bookLease.setReturnDate(bookLeaseRequestDTO.getReturnDate());
    return modelMapper.map(bookLeaseRepository.save(bookLease), BookLeaseResponseDTO.class);
  }

  public BookLeaseResponseDTO returnBook(Long bookId) {
    BookLease bookLease = bookLeaseRepository.findById(bookId).orElseThrow(
        () -> new BookLeaseDoesNotExist("Book lease with ID " + bookId + " does not exist"));
    if (bookLease.getLeaseDate() == null) {
      throw new IllegalLeaseStateException(
          String.format("Book with ID %s is not leased", bookId));
    }
    bookLease.setLeaseDate(null);
    bookLease.setReturnDate(null);
    return modelMapper.map(bookLeaseRepository.save(bookLease), BookLeaseResponseDTO.class);
  }

  public List<BookLeaseResponseDTO> getBookLeases() {
    return bookLeaseRepository.findAll().stream()
        .map(bookLease -> modelMapper.map(bookLease, BookLeaseResponseDTO.class))
        .collect(Collectors.toList());
  }

  public List<BookLeaseResponseDTO> getAvailableBooks() {
    return bookLeaseRepository.findAvailableBooks().stream()
        .map(bookLease -> modelMapper.map(bookLease, BookLeaseResponseDTO.class))
        .collect(Collectors.toList());
  }
}
