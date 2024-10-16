package com.neitex.bookstoreservice.dto;

import lombok.Data;

@Data
public class BookRequestDTO {
  private Long id;
  private String title;
  private String ISBN;
  private Long authorId;
}
