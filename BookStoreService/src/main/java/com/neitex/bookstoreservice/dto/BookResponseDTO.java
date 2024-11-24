package com.neitex.bookstoreservice.dto;

import com.neitex.bookstoreservice.entity.Author;
import lombok.Data;

@Data
public class BookResponseDTO {

  private Long id;
  private String title;
  private String isbn;
  private String description;
  private Author author;
  private String genre;
}
