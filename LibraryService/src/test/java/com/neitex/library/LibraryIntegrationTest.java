package com.neitex.library;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.nullValue;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.neitex.library.dto.BookLeaseRequestDTO;
import com.neitex.library.dto.BookLeaseResponseDTO;
import com.neitex.library.dto.BookLeaseUpdateRequestDTO;
import com.neitex.library.dto.BookUpdateType;
import com.neitex.library.repository.BookLeaseRepository;
import com.neitex.library.service.BookLeaseService;
import java.time.LocalDate;
import java.time.LocalDateTime;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@TestPropertySource(locations = "classpath:application.properties")
@SpringBootTest(classes = LibraryApplication.class)
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class LibraryIntegrationTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @Autowired
  private BookLeaseService bookLeaseService;

  @Autowired
  private BookLeaseRepository repository;
  private Long bookId = 1L;

  @Test
  @WithMockUser(roles = {"EDITOR", "VIEWER"})
  public void testGetBookLeases() throws Exception {
    mockMvc.perform(MockMvcRequestBuilders.get("/books-lease/leases")).andExpect(status().isOk())
        .andExpect(jsonPath("$").isArray());
  }

  @AfterEach
  public void cleanUp() {
    repository.deleteAll();
  }

  @BeforeEach
  public void setUp() {
    bookLeaseService.createBookLease(++bookId);
  }

  @Test
  @WithMockUser(roles = {"EDITOR", "VIEWER"})
  public void testGetBookLeaseById() throws Exception {
    mockMvc.perform(MockMvcRequestBuilders.get("/books-lease/leases/{bookId}", bookId))
        .andExpect(status().isOk()).andExpect(jsonPath("$.bookId", equalTo(bookId.intValue())))
        .andExpect(jsonPath("$.leaseDate", nullValue()))
        .andExpect(jsonPath("$.returnDate", nullValue()));
  }

  @Test
  @WithMockUser(roles = "EDITOR")
  public void testLeaseBook() throws Exception {
    BookLeaseRequestDTO leaseRequestDTO = new BookLeaseRequestDTO();
    leaseRequestDTO.setLeaseDate(LocalDate.now().atStartOfDay().plusSeconds(5));
    leaseRequestDTO.setReturnDate(LocalDate.now().plusDays(7).atStartOfDay().plusSeconds(5));
    mockMvc.perform(MockMvcRequestBuilders.post("/books-lease/leases/{bookId}/lease", bookId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(leaseRequestDTO))).andExpect(status().isOk())
        .andExpect(jsonPath("$.bookId", equalTo(bookId.intValue())))
        .andExpect(jsonPath("$.leaseDate", equalTo(leaseRequestDTO.getLeaseDate().toString())))
        .andExpect(jsonPath("$.returnDate", equalTo(leaseRequestDTO.getReturnDate().toString())));
  }

  @Test
  @WithMockUser(roles = "EDITOR")
  public void testReturnBook() throws Exception {
    BookLeaseRequestDTO leaseRequestDTO = new BookLeaseRequestDTO();
    leaseRequestDTO.setLeaseDate(LocalDateTime.now());
    leaseRequestDTO.setReturnDate(LocalDateTime.now().plusDays(7));
    BookLeaseResponseDTO lease = bookLeaseService.leaseBook(bookId, leaseRequestDTO);
    mockMvc.perform(MockMvcRequestBuilders.post("/books-lease/leases/{bookId}/return", bookId))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.bookId", equalTo(lease.getBookId().intValue())))
        .andExpect(jsonPath("$.leaseDate", equalTo(null)))
        .andExpect(jsonPath("$.returnDate", equalTo(null)));
  }

  @Test
  @WithMockUser(roles = {"VIEWER", "EDITOR"})
  public void testGetAvailableBooks() throws Exception {
    mockMvc.perform(MockMvcRequestBuilders.get("/books-lease/leases/available"))
        .andExpect(status().isOk()).andExpect(jsonPath("$").isArray());
  }

  @Test
  @WithMockUser(roles = "ADMIN")
  public void testUpdateBookCreateLease() throws Exception {
    BookLeaseUpdateRequestDTO requestDTO = new BookLeaseUpdateRequestDTO();
    requestDTO.setBookId(1L);
    requestDTO.setUpdateType(BookUpdateType.CREATED);

    mockMvc.perform(MockMvcRequestBuilders.post("/internal-books-lease/updates")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(requestDTO)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.ok").value(true));
  }

  @Test
  @WithMockUser(roles = "ADMIN")
  public void testUpdateBookDeleteLease() throws Exception {
    bookLeaseService.createBookLease(bookId + 1);
    BookLeaseUpdateRequestDTO requestDTO = new BookLeaseUpdateRequestDTO();
    requestDTO.setBookId(bookId + 1);
    requestDTO.setUpdateType(BookUpdateType.DELETED);

    mockMvc.perform(MockMvcRequestBuilders.post("/internal-books-lease/updates")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(requestDTO)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.ok").value(true));
  }
}
