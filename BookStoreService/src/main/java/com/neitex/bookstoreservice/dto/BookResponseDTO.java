package com.neitex.bookstoreservice.dto;

import com.neitex.bookstoreservice.entity.Author;
import lombok.Data;

@Data
public class BookResponseDTO {
  private Long id;
  private String title;
  private String ISBN;
  private Author author;
  private String genre;
}
