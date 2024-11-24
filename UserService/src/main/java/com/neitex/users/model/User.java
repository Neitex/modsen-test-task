package com.neitex.users.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "users")
@NoArgsConstructor
@Setter @Getter
public class User {
  @Id @GeneratedValue
  private Long id;
  private String name;
  private String login;
  private String encryptedPassword;
  @Column(columnDefinition = "VARCHAR(16)")
  private String tokenSalt;
  private UserRole role;
}
