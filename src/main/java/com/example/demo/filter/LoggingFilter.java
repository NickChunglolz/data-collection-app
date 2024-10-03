package com.example.demo.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.Builder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

@Component
public class LoggingFilter extends OncePerRequestFilter {

  private static final Logger logger = LoggerFactory.getLogger(LoggingFilter.class.getName());

  @Override
  protected void doFilterInternal(@Nullable HttpServletRequest request,
      @Nullable HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {
    assert request != null;
    assert response != null;

    ContentCachingRequestWrapper requestWrapper = new ContentCachingRequestWrapper(request);
    ContentCachingResponseWrapper responseWrapper = new ContentCachingResponseWrapper(response);

    filterChain.doFilter(requestWrapper, responseWrapper);

    logRequest(requestWrapper);
    logResponse(responseWrapper);

    responseWrapper.copyBodyToResponse();
  }

  private void logRequest(ContentCachingRequestWrapper request) {
    logger.info("REQUEST - {} ",
        RequestLog.builder().uri(request.getRequestURI()).method(request.getMethod())
            .classPath(this.getClass().getName())
            .requestBody(new String(request.getContentAsByteArray())).build());
  }

  private void logResponse(ContentCachingResponseWrapper response) {
    if (response.getStatus() >= 400) {
      logger.info("RESPONSE - [ STATUS: {}, MESSAGE: {} ]", response.getStatus(),
          HttpStatus.valueOf(response.getStatus()).getReasonPhrase());
    } else {
      logger.info("RESPONSE - {}",
          ResponseLog.builder().status(String.valueOf(response.getStatus()))
              .responseBody(new String(response.getContentAsByteArray())).build());
    }
  }

  @Builder
  private static class RequestLog {

    private String uri;
    private String method;
    private String classPath;
    private String function;
    private String requestBody;

    @Override
    public String toString() {
      return "[ URI: " + uri + ", METHOD: " + method + ", CLASS-PATH: " + classPath
          + ", REQUEST-BODY: " + requestBody + " ]";
    }
  }

  @Builder
  private static class ResponseLog {

    private String status;
    private String responseBody;

    @Override
    public String toString() {
      return "[ STATUS: " + status + ", RESPONSE-BODY: " + responseBody + " ]";
    }
  }
}
