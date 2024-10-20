package com.neitex.bookstoreservice.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.neitex.bookstoreservice.dto.AuthorRequestDTO;
import com.neitex.bookstoreservice.dto.AuthorResponseDTO;
import com.neitex.bookstoreservice.entity.Author;
import com.neitex.bookstoreservice.exception.AuthorAlreadyExistsException;
import com.neitex.bookstoreservice.exception.AuthorDoesNotExist;
import com.neitex.bookstoreservice.exception.AuthorHasBooksException;
import com.neitex.bookstoreservice.repository.AuthorRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;

import java.util.Optional;
import java.util.List;
import java.util.Collections;

class AuthorServiceTest {

  private AuthorRepository authorRepository;
  private ModelMapper modelMapper;
  private AuthorService authorService;
  private BookService bookService;

  @BeforeEach
  void setUp() {
    authorRepository = mock(AuthorRepository.class);
    bookService = mock(BookService.class);
    modelMapper = new ModelMapper();
    authorService = new AuthorService(authorRepository, bookService, modelMapper);
  }

  @Test
  void getAuthorsReturnsListOfAuthorResponseDTO() {
    Author author = new Author();
    author.setId(1L);
    author.setName("Author Name");
    when(authorRepository.findAll()).thenReturn(Collections.singletonList(author));
    AuthorResponseDTO authorResponseDTO = modelMapper.map(author, AuthorResponseDTO.class);

    List<AuthorResponseDTO> result = authorService.getAuthors();

    assertEquals(1, result.size());
    assertEquals(authorResponseDTO, result.get(0));
  }

  @Test
  void createAuthorThrowsExceptionWhenAuthorNameIsNull() {
    AuthorRequestDTO authorRequestDTO = new AuthorRequestDTO();

    assertThrows(IllegalArgumentException.class, () -> authorService.createAuthor(authorRequestDTO));
  }

  @Test
  void createAuthorThrowsExceptionWhenAuthorAlreadyExists() {
    AuthorRequestDTO authorRequestDTO = new AuthorRequestDTO();
    authorRequestDTO.setName("Existing Author");
    when(authorRepository.existsByName("Existing Author")).thenReturn(true);

    assertThrows(AuthorAlreadyExistsException.class, () -> authorService.createAuthor(authorRequestDTO));
  }

  @Test
  void createAuthorReturnsAuthorResponseDTO() {
    AuthorRequestDTO authorRequestDTO = new AuthorRequestDTO();
    authorRequestDTO.setName("New Author");
    Author author = new Author();
    author.setName("New Author");
    when(authorRepository.existsByName("New Author")).thenReturn(false);
    when(authorRepository.save(any(Author.class))).thenReturn(author);
    AuthorResponseDTO authorResponseDTO = modelMapper.map(author, AuthorResponseDTO.class);

    AuthorResponseDTO result = authorService.createAuthor(authorRequestDTO);

    assertEquals(authorResponseDTO, result);
  }

  @Test
  void updateAuthorThrowsExceptionWhenAuthorNotFound() {
    AuthorRequestDTO authorRequestDTO = new AuthorRequestDTO();
    when(authorRepository.findById(1L)).thenReturn(Optional.empty());

    assertThrows(AuthorDoesNotExist.class, () -> authorService.updateAuthor(1L, authorRequestDTO));
  }

  @Test
  void updateAuthorReturnsUpdatedAuthorResponseDTO() {
    AuthorRequestDTO authorRequestDTO = new AuthorRequestDTO();
    authorRequestDTO.setName("Updated Author");
    Author author = new Author();
    author.setId(1L);
    author.setName("Existing Author");
    when(authorRepository.findById(1L)).thenReturn(Optional.of(author));
    Author updatedAuthor = new Author();
    updatedAuthor.setId(1L);
    updatedAuthor.setName("Updated Author");
    when(authorRepository.save(any(Author.class))).thenReturn(updatedAuthor);
    AuthorResponseDTO authorResponseDTO = new AuthorResponseDTO();
    authorResponseDTO.setId(1L);
    authorResponseDTO.setName("Updated Author");

    AuthorResponseDTO result = authorService.updateAuthor(1L, authorRequestDTO);

    assertEquals(authorResponseDTO, result);
  }

  @Test
  void deleteAuthorThrowsExceptionWhenAuthorNotFound() {
    when(authorRepository.existsById(1L)).thenReturn(false);

    assertThrows(AuthorDoesNotExist.class, () -> authorService.deleteAuthor(1L));
  }

  @Test
  void deleteAuthorDeletesAuthorWhenExists() {
    when(authorRepository.existsById(1L)).thenReturn(true);
    when(bookService.countBooksByAuthor(1L)).thenReturn(0);

    authorService.deleteAuthor(1L);

    verify(authorRepository, times(1)).deleteById(1L);
  }

  @Test
  void findAuthorByIdReturnsAuthorResponseDTO() {
    Author author = new Author();
    author.setId(1L);
    when(authorRepository.findById(1L)).thenReturn(Optional.of(author));
    AuthorResponseDTO authorResponseDTO = modelMapper.map(author, AuthorResponseDTO.class);

    AuthorResponseDTO result = authorService.findAuthorById(1L);

    assertEquals(authorResponseDTO, result);
  }

  @Test
  void findAuthorByIdThrowsExceptionWhenAuthorNotFound() {
    when(authorRepository.findById(1L)).thenReturn(Optional.empty());

    assertThrows(AuthorDoesNotExist.class, () -> authorService.findAuthorById(1L));
  }

  @Test
  void deleteAuthorThrowsExceptionWhenAuthorHasBooks() {
    when(authorRepository.existsById(1L)).thenReturn(true);
    when(bookService.countBooksByAuthor(1L)).thenReturn(1);

    assertThrows(AuthorHasBooksException.class, () -> authorService.deleteAuthor(1L));
  }
}