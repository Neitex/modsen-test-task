package com.neitex.bookstoreservice.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Getter
@Setter
@Entity
@Table(name = "books")
public class Book {

  @Id
  @GeneratedValue
  private Long id;
  private String title;
  @Column(unique = true)
  private String isbn;
  @ManyToOne
  @OnDelete(action = OnDeleteAction.CASCADE)
  private Author author;
  @Column(columnDefinition = "TEXT")
  private String description;
  private String genre;
}