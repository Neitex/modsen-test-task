package com.neitex.bookstoreservice.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class BookRequestDTO {

  private String title;
  private String isbn;
  private Long authorId;
  private String genre;
}
