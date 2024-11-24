package com.neitex.library.configuration;

import java.util.Map;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.servlet.error.DefaultErrorAttributes;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.WebRequest;

@Component
public class CustomErrorAttributes extends DefaultErrorAttributes {

  @Override
  public Map<String, Object> getErrorAttributes(WebRequest webRequest,
      ErrorAttributeOptions options) {
    Map<String, Object> errorAttrs = super.getErrorAttributes(webRequest, options);
    errorAttrs.remove("path");
    errorAttrs.remove("timestamp");
    return errorAttrs;
  }
}
