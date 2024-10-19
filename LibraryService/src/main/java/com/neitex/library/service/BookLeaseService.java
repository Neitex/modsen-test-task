package com.neitex.library.service;

import com.neitex.library.dto.BookLeaseRequestDTO;
import com.neitex.library.dto.BookLeaseResponseDTO;
import com.neitex.library.exception.BookLeaseAlreadyExistsException;
import com.neitex.library.model.BookLease;
import com.neitex.library.repository.BookLeaseRepository;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service @AllArgsConstructor public class BookLeaseService {
  private final BookLeaseRepository bookLeaseRepository;
  private final ModelMapper modelMapper;

  public BookLeaseResponseDTO getBookLease(Long bookId) {
    return modelMapper.map(bookLeaseRepository.findById(bookId).orElseThrow(),
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

  public BookLeaseResponseDTO updateBookLease(Long bookId, BookLeaseRequestDTO bookLeaseRequestDTO) {
    BookLease bookLease = bookLeaseRepository.findById(bookId).orElseThrow();
    if (bookLeaseRequestDTO.getLeaseDate() != null) {
      bookLease.setLeaseDate(bookLeaseRequestDTO.getLeaseDate());
    }
    if (bookLeaseRequestDTO.getReturnDate() != null) {
      bookLease.setReturnDate(bookLeaseRequestDTO.getReturnDate());
    }
    return modelMapper.map(bookLeaseRepository.save(bookLease), BookLeaseResponseDTO.class);
  }

  public List<BookLeaseResponseDTO> getBookLeases() {
    return bookLeaseRepository.findAll().stream()
        .map(bookLease -> modelMapper.map(bookLease, BookLeaseResponseDTO.class))
        .collect(Collectors.toList());
  }
}
