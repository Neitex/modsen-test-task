package com.neitex.gateway.configuration;

import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfiguration {

  @Bean
  @Primary
  public WebClient.Builder webClient() {
    return WebClient.builder();
  }

  @Bean
  @LoadBalanced
  public WebClient.Builder lbWebClient() {
    return WebClient.builder();
  }
}
