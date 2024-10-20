package com.neitex.bookstoreservice.client;

import com.neitex.bookstoreservice.dto.BookUpdateRequestDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@FeignClient("library") public interface LibraryClient {
  @RequestMapping(value = "/internal-books-lease/updates", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
  void updateBook(BookUpdateRequestDTO bookUpdateRequestDTO);
}
