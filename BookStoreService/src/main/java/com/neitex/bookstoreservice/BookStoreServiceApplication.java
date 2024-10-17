package com.neitex.bookstoreservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class BookStoreServiceApplication {

  public static void main(String[] args) {
    SpringApplication.run(BookStoreServiceApplication.class, args);
  }
}
