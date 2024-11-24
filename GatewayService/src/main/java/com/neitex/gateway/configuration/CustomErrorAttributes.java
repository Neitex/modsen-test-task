package com.neitex.gateway.configuration;

import java.util.Map;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.reactive.error.DefaultErrorAttributes;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;


@Component
public class CustomErrorAttributes extends DefaultErrorAttributes {

  @Override
  public Map<String, Object> getErrorAttributes(ServerRequest request,
      ErrorAttributeOptions options) {
    Map<String, Object> errorAttrs = super.getErrorAttributes(request, options);
    errorAttrs.remove("path");
    errorAttrs.remove("timestamp");
    return errorAttrs;
  }
}
