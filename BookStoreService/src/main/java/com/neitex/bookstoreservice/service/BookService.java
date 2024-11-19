package com.neitex.bookstoreservice.service;

import com.neitex.bookstoreservice.client.LibraryClient;
import com.neitex.bookstoreservice.dto.BookRequestDTO;
import com.neitex.bookstoreservice.dto.BookResponseDTO;
import com.neitex.bookstoreservice.dto.BookUpdateRequestDTO;
import com.neitex.bookstoreservice.entity.Author;
import com.neitex.bookstoreservice.entity.Book;
import com.neitex.bookstoreservice.exception.AuthorDoesNotExist;
import com.neitex.bookstoreservice.exception.BookAlreadyExistsException;
import com.neitex.bookstoreservice.exception.BookDoesNotExist;
import com.neitex.bookstoreservice.exception.MissingFieldException;
import com.neitex.bookstoreservice.repository.AuthorRepository;
import com.neitex.bookstoreservice.repository.BookRepository;
import com.neitex.bookstoreservice.util.NullUtils;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class BookService {

  private final BookRepository bookRepository;
  private final AuthorRepository authorRepository;
  private final ModelMapper modelMapper;
  private final LibraryClient libraryClient;

  public Optional<BookResponseDTO> findBookById(Long id) {
    return bookRepository.findById(id).map(book -> modelMapper.map(book, BookResponseDTO.class));
  }

  public Optional<BookResponseDTO> findBookByISBN(String ISBN) {
    return bookRepository.findBookByISBN(ISBN)
        .map(book -> modelMapper.map(book, BookResponseDTO.class));
  }

  public boolean bookExistsById(Long id) {
    return bookRepository.existsById(id);
  }

  public boolean bookExistsByISBN(String ISBN) {
    return bookRepository.existsByISBN(ISBN);
  }

  public BookResponseDTO updateBook(Long id, BookRequestDTO bookRequestDTO) {
    Objects.requireNonNull(bookRequestDTO, "Book cannot be null");
    Optional<Book> existing = bookRepository.findById(id);
    if (existing.isEmpty()) {
      throw new BookDoesNotExist(String.format("Book with ID %s does not exist", id));
    }
    Book existingBook = existing.get();
    if (bookRequestDTO.getISBN() != null) {
      existingBook.setISBN(bookRequestDTO.getISBN());
    }
    if (bookRequestDTO.getTitle() != null) {
      existingBook.setTitle(bookRequestDTO.getTitle());
    }
    if (bookRequestDTO.getAuthorId() != null) {
      Optional<Author> author = authorRepository.findById(bookRequestDTO.getAuthorId());
      if (author.isEmpty()) {
        throw new AuthorDoesNotExist(
            String.format("Author with ID %s does not exist", bookRequestDTO.getAuthorId()));
      }
      existingBook.setAuthor(author.get());
    }
    Book saved = bookRepository.save(existingBook);
    return modelMapper.map(saved, BookResponseDTO.class);
  }

  public BookResponseDTO createBook(BookRequestDTO bookRequestDTO) {
    Objects.requireNonNull(bookRequestDTO, "Book cannot be null");
    if (NullUtils.anyNull(bookRequestDTO.getISBN(), bookRequestDTO.getTitle(),
        bookRequestDTO.getAuthorId())) {
      throw new MissingFieldException("ISBN, title and author ID are required");
    }
    if (bookExistsByISBN(bookRequestDTO.getISBN())) {
      throw new BookAlreadyExistsException(
          String.format("Book with ISBN %s already exists", bookRequestDTO.getISBN()));
    }
    Optional<Author> author = authorRepository.findById(bookRequestDTO.getAuthorId());
    if (author.isEmpty()) {
      throw new AuthorDoesNotExist(
          String.format("Author with ID %s does not exist", bookRequestDTO.getAuthorId()));
    }
    Book book = new Book();
    book.setISBN(bookRequestDTO.getISBN());
    book.setTitle(bookRequestDTO.getTitle());
    book.setAuthor(author.get());
    book.setGenre(bookRequestDTO.getGenre());
    Book saved = bookRepository.save(book);
    try {
      libraryClient.updateBook(
          new BookUpdateRequestDTO(saved.getId(), BookUpdateRequestDTO.BookUpdateType.CREATED));
    } catch (Exception e) {
      bookRepository.delete(saved);
      throw e;
    }
    return modelMapper.map(saved, BookResponseDTO.class);
  }

  public void deleteBook(Long id) {
    if (!bookExistsById(id)) {
      return;
    }
    libraryClient.updateBook(
        new BookUpdateRequestDTO(id, BookUpdateRequestDTO.BookUpdateType.DELETED));
    bookRepository.deleteById(id);
  }

  public List<BookResponseDTO> getBooks() {
    return bookRepository.findAll()
        .stream()
        .map(book -> modelMapper.map(book, BookResponseDTO.class))
        .toList();
  }

  public int countBooksByAuthor(Long authorId) {
    return bookRepository.countBooksByAuthorId(authorId);
  }

  public List<BookResponseDTO> findBooksByAuthor(Long authorId) {
    if (!authorRepository.existsById(authorId)) {
      throw new AuthorDoesNotExist(String.format("Author with ID %s does not exist", authorId));
    }
    return bookRepository.findBooksByAuthorId(authorId)
        .stream()
        .map(book -> modelMapper.map(book, BookResponseDTO.class))
        .toList();
  }
}
