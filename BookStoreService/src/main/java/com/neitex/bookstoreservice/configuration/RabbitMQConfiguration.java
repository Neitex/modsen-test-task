package com.neitex.bookstoreservice.configuration;

import lombok.Getter;
import lombok.Setter;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource("classpath:application.properties")
@Setter
@Getter
public class RabbitMQConfiguration {
  @Value("${bookstore.exchange.name}")
  private String exchangeName;
  @Value("${bookstore.queue.creation.name}")
  private String creationQueueName;
  @Value("${bookstore.queue.deletion.name}")
  private String deletionQueueName;

  @Bean
  public Exchange exchange() {
    return new FanoutExchange(exchangeName,true, false);
  }

  @Bean Queue creationQueue(){
    return new Queue(creationQueueName, true);
  }

  @Bean Queue deletionQueue(){
    return new Queue(deletionQueueName, true);
  }

  @Bean Binding creationBinding(Queue creationQueue, Exchange exchange) {
    return BindingBuilder.bind(creationQueue).to(exchange).with("create").noargs();
  }

  @Bean Binding deletionBinding(Queue deletionQueue, Exchange exchange) {
    return BindingBuilder.bind(deletionQueue).to(exchange).with("delete").noargs();
  }
}
