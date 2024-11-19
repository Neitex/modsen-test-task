package com.neitex.gateway.security;

import com.neitex.gateway.dto.JWTTokenDTO;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
public class JwtFilter
    extends AbstractGatewayFilterFactory<JwtFilter.Config> {

  private final WebClient.Builder webClientBuilder;

  public JwtFilter(@Qualifier("lbWebClient") WebClient.Builder webClientBuilder) {
    super(Config.class);
    this.webClientBuilder = webClientBuilder;
  }

  @Override
  public Config newConfig() {
    return new Config();
  }

  @Override
  public GatewayFilter apply(Config config) {
    return (exchange, chain) -> {
      String token = exchange.getRequest().getHeaders().getFirst("Authorization");
      if (token != null && token.startsWith("Bearer ")) {
        token = token.substring(7);
      }
      return webClientBuilder.build().post()
          .uri("http://userService/validation/validate")
          .bodyValue(new JWTTokenDTO(token))
          .retrieve()
          .bodyToMono(JWTTokenDTO.class).flatMap(internalToken -> {
            if (internalToken == null) {
              return Mono.error(new RuntimeException("Internal token is null"));
            }
            if (internalToken.getToken() == null) {
              exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
              return exchange.getResponse().setComplete();
            }
            exchange.getRequest().mutate()
                .header("Authorization", "Bearer " + internalToken.getToken());
            return chain.filter(exchange);
          });
    };
  }

  @NoArgsConstructor
  public static class Config {

  }
}
