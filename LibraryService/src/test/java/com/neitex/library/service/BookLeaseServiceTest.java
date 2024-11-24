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
import com.neitex.library.exception.BadFieldContentsException;
import com.neitex.library.exception.BookLeaseAlreadyExistsException;
import com.neitex.library.exception.BookLeaseDoesNotExist;
import com.neitex.library.exception.IllegalLeaseStateException;
import com.neitex.library.model.BookLease;
import com.neitex.library.repository.BookLeaseRepository;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
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
  void getBookLeasesReturnsListOfBookLeaseResponseDTOs() {
    BookLease bookLease = new BookLease();
    BookLeaseResponseDTO responseDTO = new BookLeaseResponseDTO();

    when(bookLeaseRepository.findAll()).thenReturn(Collections.singletonList(bookLease));
    when(modelMapper.map(bookLease, BookLeaseResponseDTO.class)).thenReturn(responseDTO);

    assertEquals(Collections.singletonList(responseDTO), bookLeaseService.getBookLeases());
  }

  @Test
  void leaseBookThrowsExceptionWhenBookLeaseDoesNotExist() {
    Long bookId = 1L;
    BookLeaseRequestDTO requestDTO = new BookLeaseRequestDTO();

    when(bookLeaseRepository.findById(bookId)).thenReturn(Optional.empty());

    assertThrows(BookLeaseDoesNotExist.class, () -> bookLeaseService.leaseBook(bookId, requestDTO));
  }

  @Test
  void leaseBookThrowsExceptionWhenBookIsAlreadyLeased() {
    Long bookId = 1L;
    BookLeaseRequestDTO requestDTO = new BookLeaseRequestDTO();
    BookLease bookLease = new BookLease();
    bookLease.setLeaseDate(LocalDateTime.now());
    bookLease.setReturnDate(LocalDateTime.now().plusDays(1));

    when(bookLeaseRepository.findById(bookId)).thenReturn(Optional.of(bookLease));

    assertThrows(
        IllegalLeaseStateException.class, () -> bookLeaseService.leaseBook(bookId, requestDTO));
  }

  @Test
  void leaseBookThrowsExceptionWhenLeaseDateIsNotSet() {
    Long bookId = 1L;
    BookLeaseRequestDTO requestDTO = new BookLeaseRequestDTO();

    BookLease bookLease = new BookLease();

    when(bookLeaseRepository.findById(bookId)).thenReturn(Optional.of(bookLease));

    assertThrows(BadFieldContentsException.class,
        () -> bookLeaseService.leaseBook(bookId, requestDTO));
  }

  @Test
  void leaseBookThrowsExceptionWhenReturnDateIsNotSet() {
    Long bookId = 1L;
    BookLeaseRequestDTO requestDTO = new BookLeaseRequestDTO();
    requestDTO.setLeaseDate(LocalDateTime.now());

    BookLease bookLease = new BookLease();

    when(bookLeaseRepository.findById(bookId)).thenReturn(Optional.of(bookLease));

    assertThrows(BadFieldContentsException.class,
        () -> bookLeaseService.leaseBook(bookId, requestDTO));
  }

  @Test
  void leaseBookThrowsExceptionWhenReturnDateIsBeforeLeaseDate() {
    Long bookId = 1L;
    BookLeaseRequestDTO requestDTO = new BookLeaseRequestDTO();
    requestDTO.setLeaseDate(LocalDateTime.now());
    requestDTO.setReturnDate(LocalDateTime.now().minusDays(1));

    BookLease bookLease = new BookLease();

    when(bookLeaseRepository.findById(bookId)).thenReturn(Optional.of(bookLease));

    assertThrows(BadFieldContentsException.class,
        () -> bookLeaseService.leaseBook(bookId, requestDTO));
  }

  @Test
  void leaseBookLeasesBookSuccessfully() {
    Long bookId = 1L;
    BookLeaseRequestDTO requestDTO = new BookLeaseRequestDTO();
    requestDTO.setLeaseDate(LocalDateTime.now());
    requestDTO.setReturnDate(LocalDateTime.now().plusDays(1));

    BookLease bookLease = new BookLease();
    BookLeaseResponseDTO responseDTO = new BookLeaseResponseDTO();

    when(bookLeaseRepository.findById(bookId)).thenReturn(Optional.of(bookLease));
    when(bookLeaseRepository.save(any(BookLease.class))).thenReturn(bookLease);
    when(modelMapper.map(bookLease, BookLeaseResponseDTO.class)).thenReturn(responseDTO);

    BookLeaseResponseDTO result = bookLeaseService.leaseBook(bookId, requestDTO);

    assertEquals(responseDTO, result);
  }

  @Test
  void returnBookThrowsExceptionWhenBookLeaseDoesNotExist() {
    Long bookId = 1L;

    when(bookLeaseRepository.findById(bookId)).thenReturn(Optional.empty());

    assertThrows(BookLeaseDoesNotExist.class, () -> bookLeaseService.returnBook(bookId));
  }

  @Test
  void returnBookThrowsExceptionWhenBookIsNotLeased() {
    Long bookId = 1L;
    BookLease bookLease = new BookLease();

    when(bookLeaseRepository.findById(bookId)).thenReturn(Optional.of(bookLease));

    assertThrows(IllegalLeaseStateException.class, () -> bookLeaseService.returnBook(bookId));
  }

  @Test
  void returnBookReturnsBookSuccessfully() {
    Long bookId = 1L;
    BookLease bookLease = new BookLease();
    bookLease.setLeaseDate(LocalDateTime.now());
    bookLease.setReturnDate(LocalDateTime.now().plusDays(1));
    BookLeaseResponseDTO responseDTO = new BookLeaseResponseDTO();

    when(bookLeaseRepository.findById(bookId)).thenReturn(Optional.of(bookLease));
    when(bookLeaseRepository.save(any(BookLease.class))).thenReturn(bookLease);
    when(modelMapper.map(bookLease, BookLeaseResponseDTO.class)).thenReturn(responseDTO);

    BookLeaseResponseDTO result = bookLeaseService.returnBook(bookId);

    assertEquals(responseDTO, result);
  }

  @Test
  void getAvailableBooksReturnsAvailableBooks() {
    BookLease bookLease1 = new BookLease();
    BookLease bookLease2 = new BookLease();
    BookLeaseResponseDTO responseDTO1 = new BookLeaseResponseDTO();
    BookLeaseResponseDTO responseDTO2 = new BookLeaseResponseDTO();

    when(bookLeaseRepository.findAvailableBooks()).thenReturn(
        Arrays.asList(bookLease1, bookLease2));
    when(modelMapper.map(bookLease1, BookLeaseResponseDTO.class)).thenReturn(responseDTO1);
    when(modelMapper.map(bookLease2, BookLeaseResponseDTO.class)).thenReturn(responseDTO2);

    List<BookLeaseResponseDTO> result = bookLeaseService.getAvailableBooks();

    assertEquals(Arrays.asList(responseDTO1, responseDTO2), result);
  }
}