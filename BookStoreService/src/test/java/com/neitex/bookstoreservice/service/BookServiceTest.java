package com.neitex.bookstoreservice.service;

import com.neitex.bookstoreservice.client.LibraryClient;
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
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.modelmapper.ModelMapper;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class BookServiceTest {

  private BookRepository bookRepository;
  private ModelMapper modelMapper;
  private LibraryClient libraryClient;
  private AuthorRepository authorRepository;
  private BookService bookService;

  @BeforeEach
  void setUp() {
    bookRepository = mock(BookRepository.class);
    authorRepository = mock(AuthorRepository.class);
    libraryClient = mock(LibraryClient.class);
    modelMapper = new ModelMapper();
    bookService = new BookService(bookRepository, authorRepository, modelMapper, libraryClient);
  }

  @Test
  void findBookByIdReturnsBookResponseDTO() {
    Book book = new Book();
    book.setId(1L);
    BookResponseDTO bookResponseDTO = new BookResponseDTO();
    bookResponseDTO.setId(1L);
    when(bookRepository.findById(1L)).thenReturn(Optional.of(book));

    Optional<BookResponseDTO> result = bookService.findBookById(1L);

    assertTrue(result.isPresent());
    assertEquals(bookResponseDTO, result.get());
  }

  @Test
  void findBookByIdReturnsEmptyWhenBookNotFound() {
    when(bookRepository.findById(1L)).thenReturn(Optional.empty());

    Optional<BookResponseDTO> result = bookService.findBookById(1L);

    assertFalse(result.isPresent());
  }

  @Test
  void findBookByISBNReturnsBookResponseDTO() {
    Book book = new Book();
    book.setISBN("1234567890");
    BookResponseDTO bookResponseDTO = new BookResponseDTO();
    bookResponseDTO.setISBN("1234567890");
    when(bookRepository.findBookByISBN("1234567890")).thenReturn(Optional.of(book));

    Optional<BookResponseDTO> result = bookService.findBookByISBN("1234567890");

    assertTrue(result.isPresent());
    assertEquals(bookResponseDTO, result.get());
  }

  @Test
  void findBookByISBNReturnsEmptyWhenBookNotFound() {
    when(bookRepository.findBookByISBN("1234567890")).thenReturn(Optional.empty());

    Optional<BookResponseDTO> result = bookService.findBookByISBN("1234567890");

    assertFalse(result.isPresent());
  }

  @Test
  void bookExistsByISBNReturnsTrueWhenBookExists() {
    when(bookRepository.existsByISBN("1234567890")).thenReturn(true);

    boolean result = bookService.bookExistsByISBN("1234567890");

    assertTrue(result);
  }

  @Test
  void updateBookUpdatesBookWhenExists() {
    Book book = new Book();
    book.setId(1L);
    book.setISBN("1234567890");
    book.setTitle("Title");
    book.setGenre("Genre");
    Author a = new Author();
    a.setId(1L);
    a.setName("Name");
    book.setAuthor(a);
    Author b = new Author();
    b.setId(2L);
    b.setName("Name2");
    when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
    when(bookRepository.save(book)).thenReturn(book);
    when(authorRepository.findById(1L)).thenReturn(Optional.of(a));
    when(authorRepository.findById(2L)).thenReturn(Optional.of(b));

    BookRequestDTO bookRequestDTO = new BookRequestDTO();
    bookRequestDTO.setISBN("123");
    bookRequestDTO.setTitle("New Title");
    bookRequestDTO.setAuthorId(2L);
    BookResponseDTO result = bookService.updateBook(1L, bookRequestDTO);

    assertEquals(1L, result.getId());
    assertEquals("123", result.getISBN());
    assertEquals("New Title", result.getTitle());
    assertEquals("Genre", result.getGenre());
    assertEquals("Name2", result.getAuthor().getName());
  }

  @Test
  void updateBookThrowsWhenBookDoesNotExist() {
    when(bookRepository.findById(1L)).thenReturn(Optional.empty());

    BookRequestDTO bookRequestDTO = new BookRequestDTO();
    bookRequestDTO.setISBN("123");

    assertThrows(BookDoesNotExist.class, () -> bookService.updateBook(1L, bookRequestDTO));
  }

  @Test
  void updateBookThrowsWhenNewAuthorDoesNotExist() {
    Book book = new Book();
    book.setId(1L);
    book.setISBN("1234567890");
    book.setTitle("Title");
    book.setGenre("Genre");
    Author a = new Author();
    a.setId(1L);
    a.setName("Name");
    book.setAuthor(a);
    when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
    when(authorRepository.findById(1L)).thenReturn(Optional.of(a));
    when(authorRepository.findById(2L)).thenReturn(Optional.empty());

    BookRequestDTO bookRequestDTO = new BookRequestDTO();
    bookRequestDTO.setISBN("123");
    bookRequestDTO.setTitle("New Title");
    bookRequestDTO.setAuthorId(2L);

    assertThrows(AuthorDoesNotExist.class, () -> bookService.updateBook(1L, bookRequestDTO));
  }

  @Test
  void deleteBookDeletesBookWhenExists() {
    when(bookRepository.existsById(1L)).thenReturn(true);

    bookService.deleteBook(1L);

    verify(bookRepository, times(1)).deleteById(1L);
    verify(libraryClient, times(1)).updateBook(any());
  }

  @Test
  void deleteBookDoesNothingWhenBookDoesNotExist() {
    when(bookRepository.existsById(1L)).thenReturn(false);

    bookService.deleteBook(1L);

    verify(bookRepository, never()).deleteById(1L);
    verify(libraryClient, never()).updateBook(any());
  }

  @Test
  void createBookSuccessfulWhenBookIsNew() {
    when(bookRepository.existsByISBN("1234567890")).thenReturn(false);
    Author a = new Author();
    a.setId(1L);
    a.setName("Name");
    when(authorRepository.findById(1L)).thenReturn(Optional.of(a));
    BookRequestDTO bookRequestDTO = new BookRequestDTO();
    bookRequestDTO.setISBN("1234567890");
    bookRequestDTO.setTitle("Title");
    bookRequestDTO.setAuthorId(1L);
    bookRequestDTO.setGenre("Genre");
    Book book = new Book();
    book.setISBN("1234567890");
    book.setTitle("Title");
    book.setGenre("Genre");
    book.setAuthor(a);
    when(bookRepository.save(Mockito.any(Book.class))).thenReturn(book);
    BookResponseDTO result = bookService.createBook(bookRequestDTO);

    assertEquals("1234567890", result.getISBN());
    assertEquals("Title", result.getTitle());
    assertEquals("Genre", result.getGenre());
    assertEquals("Name", result.getAuthor().getName());
    assertEquals(1L, result.getAuthor().getId());
  }

  @Test
  void createBookThrowsWhenAuthorDoesNotExist() {
    when(bookRepository.existsByISBN("1234567890")).thenReturn(false);
    when(authorRepository.findById(1L)).thenReturn(Optional.empty());
    BookRequestDTO bookRequestDTO = new BookRequestDTO();
    bookRequestDTO.setISBN("1234567890");
    bookRequestDTO.setTitle("Title");
    bookRequestDTO.setAuthorId(1L);
    bookRequestDTO.setGenre("Genre");

    assertThrows(AuthorDoesNotExist.class, () -> bookService.createBook(bookRequestDTO));
  }

  @Test
  void createBookThrowsWhenISBNIsTaken() {
    when(bookRepository.existsByISBN("1234567890")).thenReturn(true);
    BookRequestDTO bookRequestDTO = new BookRequestDTO();
    bookRequestDTO.setISBN("1234567890");
    bookRequestDTO.setTitle("Title");
    bookRequestDTO.setAuthorId(1L);
    bookRequestDTO.setGenre("Genre");

    assertThrows(BookAlreadyExistsException.class, () -> bookService.createBook(bookRequestDTO));
  }

  @Test
  void createBookThrowsWhenRequiredFieldsAreMissing() {
    BookRequestDTO bookRequestDTO = new BookRequestDTO();
    assertThrows(MissingFieldException.class, () -> bookService.createBook(bookRequestDTO));
    bookRequestDTO.setISBN("123");
    assertThrows(MissingFieldException.class, () -> bookService.createBook(bookRequestDTO));
    bookRequestDTO.setTitle("Title");
    assertThrows(MissingFieldException.class, () -> bookService.createBook(bookRequestDTO));
    bookRequestDTO.setAuthorId(1L);
    when(authorRepository.findById(1L)).thenReturn(Optional.of(new Author()));
    when(bookRepository.save(Mockito.any())).thenReturn(new Book());
    assertDoesNotThrow(() -> bookService.createBook(bookRequestDTO));
  }

  @Test
  void getBooksReturnsCorrectBooks() {
    Author author = new Author();
    author.setId(1L);
    author.setName("Name");
    Book bookA = new Book();
    bookA.setId(1L);
    bookA.setISBN("1");
    bookA.setAuthor(author);
    Book bookB = new Book();
    bookB.setId(2L);
    bookB.setISBN("2");
    bookB.setAuthor(author);
    Book bookC = new Book();
    bookC.setId(3L);
    bookC.setISBN("3");
    bookC.setAuthor(author);
    when(bookRepository.findAll()).thenReturn(List.of(bookA, bookB, bookC));
    assertEquals(3, bookService.getBooks().size());
    assertArrayEquals(List.of(modelMapper.map(bookA, BookResponseDTO.class),
            modelMapper.map(bookB, BookResponseDTO.class), modelMapper.map(bookC, BookResponseDTO.class)).toArray(),
        bookService.getBooks().toArray());
  }
}