package com.neitex.bookstoreservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class BookUpdateRequestDTO {

  private Long bookId;
  private BookUpdateType updateType;

  public enum BookUpdateType {
    CREATED, DELETED,
  }
}
