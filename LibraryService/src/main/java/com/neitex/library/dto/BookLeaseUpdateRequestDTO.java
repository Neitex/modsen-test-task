package com.neitex.library.dto;

import lombok.Data;

@Data
public class BookLeaseUpdateRequestDTO {

  private Long bookId;
  private BookUpdateType updateType;
}
