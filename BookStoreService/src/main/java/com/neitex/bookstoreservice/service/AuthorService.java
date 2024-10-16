package com.neitex.bookstoreservice.service;

import com.neitex.bookstoreservice.dto.AuthorRequestDTO;
import com.neitex.bookstoreservice.dto.AuthorResponseDTO;
import com.neitex.bookstoreservice.entity.Author;
import com.neitex.bookstoreservice.exception.AuthorAlreadyExistsException;
import com.neitex.bookstoreservice.exception.AuthorDoesNotExist;
import com.neitex.bookstoreservice.repository.AuthorRepository;
import java.util.List;
import java.util.Objects;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class AuthorService {
  private final AuthorRepository authorRepository;
  private final ModelMapper modelMapper;

  public List<AuthorResponseDTO> getAuthors() {
    return authorRepository.findAll().stream()
        .map(author -> modelMapper.map(author, AuthorResponseDTO.class))
        .toList();
  }

  public AuthorResponseDTO createAuthor(AuthorRequestDTO author) {
    Objects.requireNonNull(author);
    if (author.getName() == null){
      throw new IllegalArgumentException("Author name cannot be null");
    }
    if (authorRepository.existsByName(author.getName())) {
      throw new AuthorAlreadyExistsException("Author with name " + author.getName() + " already exists");
    }
    Author newAuthor = new Author();
    newAuthor.setName(author.getName());
    newAuthor = authorRepository.save(newAuthor);
    return modelMapper.map(newAuthor, AuthorResponseDTO.class);
  }

  public AuthorResponseDTO updateAuthor(Long id, AuthorRequestDTO author) {
    Objects.requireNonNull(id);
    Objects.requireNonNull(author);
    Author existingAuthor = authorRepository.findById(id)
        .orElseThrow(() -> new AuthorDoesNotExist("Author with id " + id + " not found"));
    if (author.getName() != null) {
      existingAuthor.setName(author.getName());
    }
    existingAuthor = authorRepository.save(existingAuthor);
    return modelMapper.map(existingAuthor, AuthorResponseDTO.class);
  }

  public void deleteAuthor(Long id) {
    Objects.requireNonNull(id);
    if (!authorRepository.existsById(id)) {
      throw new AuthorDoesNotExist("Author with id " + id + " not found");
    }
    authorRepository.deleteById(id);
  }

  public AuthorResponseDTO findAuthorById(Long id) {
    Objects.requireNonNull(id);
    Author author = authorRepository.findById(id)
        .orElseThrow(() -> new AuthorDoesNotExist("Author with id " + id + " not found"));
    return modelMapper.map(author, AuthorResponseDTO.class);
  }
}
