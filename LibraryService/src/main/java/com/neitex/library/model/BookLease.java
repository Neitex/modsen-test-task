package com.neitex.library.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@NoArgsConstructor
@Getter
@Setter
@Table(name = "book_lease")
public class BookLease {
  @Id
  private Long bookId;
  private LocalDateTime leaseDate;
  private LocalDateTime returnDate;
}
