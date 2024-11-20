package com.neitex.gateway.security;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.neitex.gateway.dto.JWTTokenDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
class JwtFilterTest {

  @Mock
  private WebClient.Builder webClientBuilder;

  @Mock
  private WebClient webClient;

  @Mock
  private WebClient.RequestBodyUriSpec requestBodyUriSpec;

  @Mock
  private WebClient.ResponseSpec responseSpec;

  @Mock
  private GatewayFilterChain filterChain;

  private JwtFilter jwtFilter;

  @BeforeEach
  void setUp() {
    jwtFilter = new JwtFilter(webClientBuilder);
  }

  @SuppressWarnings("unchecked")
  private void setupWebClientMock() {
    when(webClientBuilder.build()).thenReturn(webClient);
    when(webClient.post()).thenReturn(requestBodyUriSpec);
    when(requestBodyUriSpec.uri(anyString())).thenReturn(requestBodyUriSpec);
    when(requestBodyUriSpec.bodyValue(any())).thenReturn(
        (WebClient.RequestHeadersSpec) requestBodyUriSpec);
    when(requestBodyUriSpec.retrieve()).thenReturn(responseSpec);
  }

  @Test
  void shouldPassValidToken() {
    // Given
    setupWebClientMock();
    String validToken = "valid-jwt-token";
    String validInternalToken = "valid-internal-token";
    MockServerHttpRequest request = MockServerHttpRequest.get("/")
        .header(HttpHeaders.AUTHORIZATION, "Bearer " + validToken)
        .build();
    MockServerWebExchange exchange = MockServerWebExchange.from(request);

    when(responseSpec.bodyToMono(JWTTokenDTO.class))
        .thenReturn(Mono.just(new JWTTokenDTO(validInternalToken)));
    when(filterChain.filter(exchange)).thenReturn(Mono.empty());

    // When
    GatewayFilter filter = jwtFilter.apply(new JwtFilter.Config());
    Mono<Void> result = filter.filter(exchange, filterChain);

    // Then
    StepVerifier.create(result)
        .verifyComplete();

    verify(filterChain).filter(exchange);
    verify(requestBodyUriSpec).uri("http://userService/validation/validate");
    verify(requestBodyUriSpec).bodyValue(new JWTTokenDTO(validToken));
  }

  @Test
  void shouldRejectRequestWithoutToken() {
    // Given
    MockServerHttpRequest request = MockServerHttpRequest.get("/").build();
    MockServerWebExchange exchange = MockServerWebExchange.from(request);

    // When
    GatewayFilter filter = jwtFilter.apply(new JwtFilter.Config());
    Mono<Void> result = filter.filter(exchange, filterChain);

    // Then
    StepVerifier.create(result)
        .verifyComplete();

    verify(filterChain, never()).filter(any());
    assertThat(exchange.getResponse().getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
  }

  @Test
  void shouldRejectInvalidToken() {
    // Given
    setupWebClientMock();
    String invalidToken = "invalid-token";
    MockServerHttpRequest request = MockServerHttpRequest.get("/")
        .header(HttpHeaders.AUTHORIZATION, "Bearer " + invalidToken)
        .build();
    MockServerWebExchange exchange = MockServerWebExchange.from(request);

    when(responseSpec.bodyToMono(JWTTokenDTO.class))
        .thenReturn(Mono.just(new JWTTokenDTO(null)));

    // When
    GatewayFilter filter = jwtFilter.apply(new JwtFilter.Config());
    Mono<Void> result = filter.filter(exchange, filterChain);

    // Then
    StepVerifier.create(result)
        .verifyComplete();

    verify(filterChain, never()).filter(any());
    assertThat(exchange.getResponse().getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
  }

  @Test
  void shouldHandleNullResponseFromValidationService() {
    // Given
    setupWebClientMock();
    String token = "some-token";
    MockServerHttpRequest request = MockServerHttpRequest.get("/")
        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
        .build();
    MockServerWebExchange exchange = MockServerWebExchange.from(request);

    when(responseSpec.bodyToMono(JWTTokenDTO.class))
        .thenReturn(Mono.empty());  // Changed from Mono.just(null) to Mono.empty()

    // When
    GatewayFilter filter = jwtFilter.apply(new JwtFilter.Config());
    Mono<Void> result = filter.filter(exchange, filterChain);

    // Then
    StepVerifier.create(result)
        .expectError(RuntimeException.class)
        .verify();

    verify(filterChain, never()).filter(any());
  }
}