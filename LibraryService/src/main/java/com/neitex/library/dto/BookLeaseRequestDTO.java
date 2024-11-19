package com.neitex.library.dto;

import java.time.LocalDateTime;
import lombok.Data;

@Data
public class BookLeaseRequestDTO {

  private LocalDateTime leaseDate;
  private LocalDateTime returnDate;
}
