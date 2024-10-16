package com.neitex.bookstoreservice;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.neitex.bookstoreservice.dto.BookRequestDTO;
import com.neitex.bookstoreservice.dto.BookResponseDTO;
import com.neitex.bookstoreservice.entity.Author;
import com.neitex.bookstoreservice.entity.Book;
import com.neitex.bookstoreservice.exception.AuthorDoesNotExist;
import com.neitex.bookstoreservice.exception.BookAlreadyExistsException;
import com.neitex.bookstoreservice.exception.BookDoesNotExist;
import com.neitex.bookstoreservice.exception.MissingFieldException;
import com.neitex.bookstoreservice.repository.AuthorRepository;
import com.neitex.bookstoreservice.repository.BookRepository;
import com.neitex.bookstoreservice.service.BookService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import java.util.Optional;

class BookServiceTest {

  private BookRepository bookRepository;
  private AuthorRepository authorRepository;
  private RabbitTemplate rabbitTemplate;
  private Exchange exchange;
  private ModelMapper modelMapper;
  private BookService bookService;

  @BeforeEach
  void setUp() {
    bookRepository = mock(BookRepository.class);
    authorRepository = mock(AuthorRepository.class);
    rabbitTemplate = mock(RabbitTemplate.class);
    exchange = mock(Exchange.class);
    modelMapper = new ModelMapper();
    bookService = new BookService(bookRepository, authorRepository, rabbitTemplate, exchange, modelMapper);
  }

  @Test
  void createBookSuccessfully() {
    BookRequestDTO bookRequestDTO = new BookRequestDTO();
    bookRequestDTO.setISBN("1234567890");
    bookRequestDTO.setTitle("Test Book");
    bookRequestDTO.setAuthorId(1L);

    Author author = new Author();
    author.setId(1L);

    when(authorRepository.findById(1L)).thenReturn(Optional.of(author));
    when(bookRepository.existsByISBN("1234567890")).thenReturn(false);

    Book book = new Book();
    book.setISBN("1234567890");
    book.setTitle("Test Book");
    book.setAuthor(author);

    when(bookRepository.save(any(Book.class))).thenReturn(book);

    BookResponseDTO response = bookService.createBook(bookRequestDTO);

    assertNotNull(response);
    assertEquals("1234567890", response.getISBN());
    assertEquals("Test Book", response.getTitle());
    assertEquals(author, response.getAuthor());
  }

  @Test
  void createBookThrowsMissingFieldException() {
    BookRequestDTO bookRequestDTO = new BookRequestDTO();
    bookRequestDTO.setISBN(null);
    bookRequestDTO.setTitle("Test Book");
    bookRequestDTO.setAuthorId(1L);

    assertThrows(MissingFieldException.class, () -> bookService.createBook(bookRequestDTO));
  }

  @Test
  void createBookThrowsBookAlreadyExistsException() {
    BookRequestDTO bookRequestDTO = new BookRequestDTO();
    bookRequestDTO.setISBN("1234567890");
    bookRequestDTO.setTitle("Test Book");
    bookRequestDTO.setAuthorId(1L);

    when(bookRepository.existsByISBN("1234567890")).thenReturn(true);

    assertThrows(BookAlreadyExistsException.class, () -> bookService.createBook(bookRequestDTO));
  }

  @Test
  void createBookThrowsAuthorDoesNotExist() {
    BookRequestDTO bookRequestDTO = new BookRequestDTO();
    bookRequestDTO.setISBN("1234567890");
    bookRequestDTO.setTitle("Test Book");
    bookRequestDTO.setAuthorId(1L);

    when(bookRepository.existsByISBN("1234567890")).thenReturn(false);
    when(authorRepository.findById(1L)).thenReturn(Optional.empty());

    assertThrows(AuthorDoesNotExist.class, () -> bookService.createBook(bookRequestDTO));
  }

  @Test
  void updateBookSuccessfully() {
    BookRequestDTO bookRequestDTO = new BookRequestDTO();
    bookRequestDTO.setISBN("1234567890");
    bookRequestDTO.setTitle("Updated Book");
    bookRequestDTO.setAuthorId(1L);

    Author author = new Author();
    author.setId(1L);

    Book existingBook = new Book();
    existingBook.setId(1L);
    existingBook.setISBN("1234567890");
    existingBook.setTitle("Test Book");
    existingBook.setAuthor(author);

    when(bookRepository.findById(1L)).thenReturn(Optional.of(existingBook));
    when(authorRepository.findById(1L)).thenReturn(Optional.of(author));
    when(bookRepository.save(any(Book.class))).thenReturn(existingBook);

    BookResponseDTO response = bookService.updateBook(1L, bookRequestDTO);

    assertNotNull(response);
    assertEquals("1234567890", response.getISBN());
    assertEquals("Updated Book", response.getTitle());
    assertEquals(author, response.getAuthor());
  }

  @Test
  void updateBookThrowsBookDoesNotExist() {
    BookRequestDTO bookRequestDTO = new BookRequestDTO();
    bookRequestDTO.setISBN("1234567890");
    bookRequestDTO.setTitle("Updated Book");
    bookRequestDTO.setAuthorId(1L);

    when(bookRepository.findById(1L)).thenReturn(Optional.empty());

    assertThrows(BookDoesNotExist.class, () -> bookService.updateBook(1L, bookRequestDTO));
  }

  @Test
  void updateBookThrowsAuthorDoesNotExist() {
    BookRequestDTO bookRequestDTO = new BookRequestDTO();
    bookRequestDTO.setISBN("1234567890");
    bookRequestDTO.setTitle("Updated Book");
    bookRequestDTO.setAuthorId(1L);

    Author author = new Author();
    author.setId(1L);

    Book existingBook = new Book();
    existingBook.setId(1L);
    existingBook.setISBN("1234567890");
    existingBook.setTitle("Test Book");
    existingBook.setAuthor(author);

    when(bookRepository.findById(1L)).thenReturn(Optional.of(existingBook));
    when(authorRepository.findById(1L)).thenReturn(Optional.empty());

    assertThrows(AuthorDoesNotExist.class, () -> bookService.updateBook(1L, bookRequestDTO));
  }
}