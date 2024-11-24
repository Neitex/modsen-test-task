package com.neitex.library.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.neitex.library.dto.BookLeaseRequestDTO;
import com.neitex.library.dto.BookLeaseResponseDTO;
import com.neitex.library.exception.BookLeaseAlreadyExistsException;
import com.neitex.library.exception.BookLeaseDoesNotExist;
import com.neitex.library.model.BookLease;
import com.neitex.library.repository.BookLeaseRepository;
import java.util.Collections;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;

class BookLeaseServiceTest {

  private BookLeaseRepository bookLeaseRepository;

  private ModelMapper modelMapper;

  private BookLeaseService bookLeaseService;

  @BeforeEach
  void setUp() {
    bookLeaseRepository = mock(BookLeaseRepository.class);
    modelMapper = mock(ModelMapper.class);
    bookLeaseService = new BookLeaseService(bookLeaseRepository, modelMapper);
  }

  @Test
  void getBookLeaseReturnsBookLeaseResponseDTOWhenBookLeaseExists() {
    Long bookId = 1L;
    BookLease bookLease = new BookLease();
    bookLease.setBookId(bookId);
    BookLeaseResponseDTO responseDTO = new BookLeaseResponseDTO();

    when(bookLeaseRepository.findById(bookId)).thenReturn(Optional.of(bookLease));
    when(modelMapper.map(bookLease, BookLeaseResponseDTO.class)).thenReturn(responseDTO);

    BookLeaseResponseDTO result = bookLeaseService.getBookLease(bookId);

    assertEquals(responseDTO, result);
  }

  @Test
  void getBookLeaseThrowsExceptionWhenBookLeaseDoesNotExist() {
    Long bookId = 1L;

    when(bookLeaseRepository.findById(bookId)).thenReturn(Optional.empty());

    assertThrows(BookLeaseDoesNotExist.class, () -> bookLeaseService.getBookLease(bookId));
  }

  @Test
  void createBookLeaseThrowsExceptionWhenBookLeaseAlreadyExists() {
    Long bookId = 1L;

    when(bookLeaseRepository.existsById(bookId)).thenReturn(true);

    assertThrows(BookLeaseAlreadyExistsException.class,
        () -> bookLeaseService.createBookLease(bookId));
  }

  @Test
  void createBookLeaseSavesNewBookLeaseWhenItDoesNotExist() {
    Long bookId = 1L;

    when(bookLeaseRepository.existsById(bookId)).thenReturn(false);

    bookLeaseService.createBookLease(bookId);

    verify(bookLeaseRepository, times(1)).save(any(BookLease.class));
  }

  @Test
  void deleteBookLeaseDeletesBookLease() {
    Long bookId = 1L;

    bookLeaseService.deleteBookLease(bookId);

    verify(bookLeaseRepository, times(1)).deleteById(bookId);
  }

  @Test
  void updateBookLeaseUpdatesAndReturnsBookLeaseResponseDTOWhenBookLeaseExists() {
    Long bookId = 1L;
    BookLeaseRequestDTO requestDTO = new BookLeaseRequestDTO();
    BookLease bookLease = new BookLease();
    BookLeaseResponseDTO responseDTO = new BookLeaseResponseDTO();

    when(bookLeaseRepository.findById(bookId)).thenReturn(Optional.of(bookLease));
    when(bookLeaseRepository.save(bookLease)).thenReturn(bookLease);
    when(modelMapper.map(bookLease, BookLeaseResponseDTO.class)).thenReturn(responseDTO);

    BookLeaseResponseDTO result = bookLeaseService.updateBookLease(bookId, requestDTO);

    assertEquals(responseDTO, result);
  }

  @Test
  void updateBookLeaseThrowsExceptionWhenBookLeaseDoesNotExist() {
    Long bookId = 1L;
    BookLeaseRequestDTO requestDTO = new BookLeaseRequestDTO();

    when(bookLeaseRepository.findById(bookId)).thenReturn(Optional.empty());

    assertThrows(BookLeaseDoesNotExist.class,
        () -> bookLeaseService.updateBookLease(bookId, requestDTO));
  }

  @Test
  void getBookLeasesReturnsListOfBookLeaseResponseDTOs() {
    BookLease bookLease = new BookLease();
    BookLeaseResponseDTO responseDTO = new BookLeaseResponseDTO();

    when(bookLeaseRepository.findAll()).thenReturn(Collections.singletonList(bookLease));
    when(modelMapper.map(bookLease, BookLeaseResponseDTO.class)).thenReturn(responseDTO);

    assertEquals(Collections.singletonList(responseDTO), bookLeaseService.getBookLeases());
  }
}