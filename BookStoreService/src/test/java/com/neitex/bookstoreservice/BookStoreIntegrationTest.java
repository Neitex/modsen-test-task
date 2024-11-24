package com.neitex.bookstoreservice;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.neitex.bookstoreservice.configuration.WireMockConfig;
import com.neitex.bookstoreservice.dto.AuthorRequestDTO;
import com.neitex.bookstoreservice.dto.AuthorResponseDTO;
import com.neitex.bookstoreservice.dto.BookRequestDTO;
import com.neitex.bookstoreservice.dto.BookResponseDTO;
import com.neitex.bookstoreservice.exception.BookDoesNotExist;
import com.neitex.bookstoreservice.service.AuthorService;
import com.neitex.bookstoreservice.service.BookService;
import java.io.IOException;
import org.apache.commons.lang.RandomStringUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@TestPropertySource(locations = "classpath:application.properties")
@SpringBootTest(classes = BookStoreServiceApplication.class)
@ActiveProfiles("test")
@Import(WireMockConfig.class)
@AutoConfigureMockMvc
public class BookStoreIntegrationTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @Autowired
  private AuthorService authorService;

  @Autowired
  private WireMockServer wireMockServer;

  @Autowired
  private BookService bookService;

  private AuthorResponseDTO author;

  @BeforeEach
  void setUp() throws IOException {
    wireMockServer.stubFor(
        WireMock.post(WireMock.urlEqualTo("/internal-books-lease/updates"))
            .willReturn(WireMock.aResponse()
                .withStatus(HttpStatus.OK.value())
                .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                .withBody("{\"ok\": true}")));
  }

  @AfterEach
  void tearDown() {
    wireMockServer.resetAll();
  }

  @Test
  @WithMockUser(roles = {"EDITOR", "VIEWER"})
  public void testGetAuthors() throws Exception {
    mockMvc.perform(MockMvcRequestBuilders.get("/authors"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$").isArray());
  }

  @Test
  @WithMockUser(roles = {"EDITOR", "VIEWER"})
  public void testGetAuthorById() throws Exception {
    AuthorRequestDTO requestDTO = new AuthorRequestDTO();
    requestDTO.setName("testGetAuthorById");
    AuthorResponseDTO author = authorService.createAuthor(requestDTO);
    mockMvc.perform(MockMvcRequestBuilders.get("/authors/{id}", author.getId()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.name", equalTo("testGetAuthorById")));
  }

  @Test
  @WithMockUser(roles = "EDITOR")
  public void testCreateAuthor() throws Exception {
    AuthorRequestDTO requestDTO = new AuthorRequestDTO();
    requestDTO.setName("testCreateAuthor");
    mockMvc.perform(MockMvcRequestBuilders.post("/authors")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(requestDTO)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.name", equalTo("testCreateAuthor")));
  }

  @Test
  @WithMockUser(roles = "EDITOR")
  public void testUpdateAuthor() throws Exception {
    AuthorRequestDTO requestDTO = new AuthorRequestDTO();
    requestDTO.setName("testUpdateAuthor");
    AuthorResponseDTO author = authorService.createAuthor(requestDTO);
    AuthorRequestDTO updatedAuthor = new AuthorRequestDTO();
    updatedAuthor.setName("testUpdateAuthor updated");
    mockMvc.perform(MockMvcRequestBuilders.patch("/authors/{id}", author.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(updatedAuthor)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.name", equalTo("testUpdateAuthor updated")));
  }

  @Test
  @WithMockUser(roles = "EDITOR")
  public void testDeleteAuthor() throws Exception {
    AuthorRequestDTO requestDTO = new AuthorRequestDTO();
    requestDTO.setName("testDeleteAuthor");
    AuthorResponseDTO author = authorService.createAuthor(requestDTO);
    mockMvc.perform(MockMvcRequestBuilders.delete("/authors/{id}", author.getId()))
        .andExpect(status().isNoContent());
  }

  @Test
  @WithMockUser(roles = {"EDITOR", "VIEWER"})
  public void testGetBooks() throws Exception {
    mockMvc.perform(MockMvcRequestBuilders.get("/books"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$").isArray());
  }

  private AuthorResponseDTO createAuthor() {
    AuthorRequestDTO requestDTO = new AuthorRequestDTO();
    requestDTO.setName(RandomStringUtils.randomAlphanumeric(16));
    return authorService.createAuthor(requestDTO);
  }

  @Test
  @WithMockUser(roles = {"EDITOR", "VIEWER"})
  public void testGetBookById() throws Exception {
    AuthorResponseDTO author = createAuthor();
    BookRequestDTO requestDTO = new BookRequestDTO();
    requestDTO.setTitle("Test Book");
    requestDTO.setIsbn("getBookById");
    requestDTO.setDescription("Test Description");
    requestDTO.setAuthorId(author.getId());
    requestDTO.setGenre("Test Genre");
    BookResponseDTO book = bookService.createBook(requestDTO);
    mockMvc.perform(MockMvcRequestBuilders.get("/books/{id}", book.getId()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.title", equalTo("Test Book")))
        .andExpect(jsonPath("$.isbn", equalTo("getBookById")))
        .andExpect(jsonPath("$.description", equalTo("Test Description")))
        .andExpect(jsonPath("$.author.id", equalTo(author.getId().intValue())))
        .andExpect(jsonPath("$.author.name", equalTo(author.getName())))
        .andExpect(jsonPath("$.genre", equalTo("Test Genre")));
  }

  @Test
  @WithMockUser(roles = {"EDITOR", "VIEWER"})
  public void testGetBookByIsbn() throws Exception {
    AuthorResponseDTO author = createAuthor();
    BookRequestDTO requestDTO = new BookRequestDTO();
    requestDTO.setTitle("Test Book");
    requestDTO.setIsbn("getBookByIsbn");
    requestDTO.setDescription("Test Description");
    requestDTO.setAuthorId(author.getId());
    requestDTO.setGenre("Test Genre");
    BookResponseDTO book = bookService.createBook(requestDTO);
    mockMvc.perform(MockMvcRequestBuilders.get("/books/by-isbn/{isbn}", book.getIsbn()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.title", equalTo("Test Book")))
        .andExpect(jsonPath("$.isbn", equalTo("getBookByIsbn")))
        .andExpect(jsonPath("$.description", equalTo("Test Description")))
        .andExpect(jsonPath("$.author.id", equalTo(author.getId().intValue())))
        .andExpect(jsonPath("$.genre", equalTo("Test Genre")));
  }

  @Test
  @WithMockUser(roles = {"EDITOR", "VIEWER"})
  public void testGetBooksByAuthor() throws Exception {
    AuthorResponseDTO author = createAuthor();
    BookRequestDTO requestDTO = new BookRequestDTO();
    requestDTO.setTitle("Test Book");
    requestDTO.setIsbn("getBooksByAuthor");
    requestDTO.setDescription("Test Description");
    requestDTO.setAuthorId(author.getId());
    requestDTO.setGenre("Test Genre");
    bookService.createBook(requestDTO);
    mockMvc.perform(MockMvcRequestBuilders.get("/books/by-author/{authorId}", 1L))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$").isArray()).andExpect(jsonPath("$.length()", equalTo(1)))
        .andExpect(jsonPath("$[0].author.id", equalTo(author.getId().intValue())));
  }

  @Test
  @WithMockUser(roles = "EDITOR")
  public void testCreateBook() throws Exception {
    AuthorResponseDTO author = createAuthor();
    BookRequestDTO requestDTO = new BookRequestDTO();
    requestDTO.setTitle("New Book");
    requestDTO.setIsbn("createBook");
    requestDTO.setDescription("New Description");
    requestDTO.setAuthorId(author.getId());
    requestDTO.setGenre("New Genre");
    mockMvc.perform(MockMvcRequestBuilders.post("/books")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(requestDTO)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.title", equalTo("New Book")))
        .andExpect(jsonPath("$.isbn", equalTo("createBook")))
        .andExpect(jsonPath("$.description", equalTo("New Description")))
        .andExpect(jsonPath("$.author.id", equalTo(author.getId().intValue())))
        .andExpect(jsonPath("$.genre", equalTo("New Genre")));
  }

  @Test
  @WithMockUser(roles = "EDITOR")
  public void testUpdateBook() throws Exception {
    AuthorResponseDTO author = createAuthor();
    BookRequestDTO requestDTO = new BookRequestDTO();
    requestDTO.setTitle("Old Book");
    requestDTO.setIsbn("updateBook");
    requestDTO.setDescription("Old Description");
    requestDTO.setAuthorId(author.getId());
    requestDTO.setGenre("Old Genre");
    BookResponseDTO book = bookService.createBook(requestDTO);
    BookRequestDTO updatedRequestDTO = new BookRequestDTO();
    AuthorResponseDTO newAuthor = createAuthor();
    updatedRequestDTO.setTitle("Updated Book");
    updatedRequestDTO.setIsbn("updateBook2");
    updatedRequestDTO.setDescription("Updated Description");
    updatedRequestDTO.setAuthorId(newAuthor.getId());
    updatedRequestDTO.setGenre("Updated Genre");
    mockMvc.perform(MockMvcRequestBuilders.post("/books/{id}", book.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(updatedRequestDTO)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.title", equalTo("Updated Book")))
        .andExpect(jsonPath("$.isbn", equalTo("updateBook2")))
        .andExpect(jsonPath("$.description", equalTo("Updated Description")))
        .andExpect(jsonPath("$.author.id", equalTo(newAuthor.getId().intValue())))
        .andExpect(jsonPath("$.genre", equalTo("Updated Genre")));
  }

  @Test
  @WithMockUser(roles = "EDITOR")
  public void testDeleteBook() throws Exception {
    AuthorResponseDTO author = createAuthor();
    BookRequestDTO requestDTO = new BookRequestDTO();
    requestDTO.setTitle("Book to Delete");
    requestDTO.setIsbn("deleteBook");
    requestDTO.setDescription("Description to Delete");
    requestDTO.setAuthorId(author.getId());
    requestDTO.setGenre("Genre to Delete");
    BookResponseDTO book = bookService.createBook(requestDTO);
    mockMvc.perform(MockMvcRequestBuilders.delete("/books/{id}", book.getId()))
        .andExpect(status().isNoContent());
    assertThrows(BookDoesNotExist.class, () -> bookService.getBookByID(book.getId()));
  }
}
