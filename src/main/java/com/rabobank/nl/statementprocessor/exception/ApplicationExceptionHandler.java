package com.rabobank.nl.statementprocessor.exception;

import com.rabobank.nl.statementprocessor.api.model.StatementResult;
import java.util.Collections;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Transforms exceptions to the error response structure
 */
@ControllerAdvice
@Slf4j
public class ApplicationExceptionHandler {

  /**
   * Handle validation exceptions
   *
   * @param exception the exception
   * @return ResponseEntity with error messages
   */
  @ExceptionHandler({HttpMessageNotReadableException.class})
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ResponseEntity<Object> handleJsonParsingException(
      HttpMessageNotReadableException exception) {
    final String logMethod = "handleJsonParsingException(exception):ResponseEntity Exception Cause :%s";
    log.error(String.format(logMethod, exception.getCause()));

    StatementResult statementResultBadRequest = StatementResult.builder().result("BAD_REQUEST")
        .errorRecords(Collections.EMPTY_LIST).build();

    return new ResponseEntity(statementResultBadRequest, HttpHeaders.EMPTY, HttpStatus.BAD_REQUEST);
  }

  /**
   * Handle any other exception response
   *
   * @param exception the exception
   * @return ResponseEntity with error messages
   */
  @ExceptionHandler({Exception.class})
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  public ResponseEntity<Object> handleAnyOtherSituationException(Exception exception) {

    final String logMethod = "handleAnyOtherSituationException(exception):ResponseEntity Exception Cause : %s";
    log.error(String.format(logMethod,
        null != exception.getCause() ? exception.getCause() : exception.getMessage()));

    StatementResult statementResultBadRequest = StatementResult.builder()
        .result("INTERNAL_SERVER_ERROR").errorRecords(Collections.EMPTY_LIST).build();

    return new ResponseEntity(statementResultBadRequest, HttpHeaders.EMPTY,
        HttpStatus.INTERNAL_SERVER_ERROR);
  }

}
