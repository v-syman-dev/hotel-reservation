package by.vladislav.hotelreservation.handler;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import by.vladislav.hotelreservation.entity.dto.ErrorResponse;
import by.vladislav.hotelreservation.exception.EntityAlreadyExistsException;
import by.vladislav.hotelreservation.exception.EntityNotFoundException;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;

@RestControllerAdvice
@Slf4j
public class GlobalExceptonHandler {

  @ExceptionHandler(EntityNotFoundException.class)
  public ResponseEntity<ErrorResponse> handleNotFound(EntityNotFoundException exception) {
    log.error("Handled not found exception: {}", exception.getMessage());
    return buildResponse(HttpStatus.NOT_FOUND, exception.getMessage(), null);
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex) {
    List<String> errors = ex.getBindingResult().getFieldErrors().stream()
        .map(error -> error.getField() + ": " + error.getDefaultMessage())
        .toList();
    log.error("Handled validation exception: {}", String.join("; ", errors));
    return buildResponse(HttpStatus.BAD_REQUEST, "Validation failed", errors);
  }

  @ExceptionHandler(HandlerMethodValidationException.class)
  public ResponseEntity<ErrorResponse> handleMethodValidation(HandlerMethodValidationException ex) {
    List<String> errors = extractMethodValidationErrors(ex);
    log.error("Handled method validation exception: {}", String.join("; ", errors));
    return buildResponse(HttpStatus.BAD_REQUEST, "Validation failed", errors);
  }

  @ExceptionHandler(ConstraintViolationException.class)
  public ResponseEntity<ErrorResponse> handleConstraintViolation(ConstraintViolationException ex) {
    List<String> errors = ex.getConstraintViolations().stream()
        .map(violation -> violation.getPropertyPath() + ": " + violation.getMessage())
        .toList();
    log.error("Handled constraint violation exception: {}", String.join("; ", errors));
    return buildResponse(HttpStatus.BAD_REQUEST, "Validation failed", errors);
  }

  @ExceptionHandler(EntityAlreadyExistsException.class)
  public ResponseEntity<ErrorResponse> handleEntities(EntityAlreadyExistsException ex) {
    log.error("Handled already exists exception: {}", ex.getMessage());
    return buildResponse(HttpStatus.BAD_REQUEST, ex.getMessage(), null);
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorResponse> handleAll(Exception ex) {
    log.error("Handled unexpected exception [{}]: {}", ex.getClass().getSimpleName(), ex.getMessage());
    return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred", List.of(ex.getMessage()));
  }

  private ResponseEntity<ErrorResponse> buildResponse(HttpStatus status, String message, List<String> details) {
    ErrorResponse response = new ErrorResponse(
        LocalDateTime.now(),
        status.value(),
        status.getReasonPhrase(),
        message,
        details);
    return new ResponseEntity<>(response, status);
  }

  private List<String> extractMethodValidationErrors(HandlerMethodValidationException ex) {
    try {
      Object validationResults = ex.getClass()
          .getMethod("getAllValidationResults")
          .invoke(ex);

      if (validationResults instanceof List<?> results) {
        List<String> errors = new ArrayList<>();

        for (Object result : results) {
          Object resolvableErrors = result.getClass()
              .getMethod("getResolvableErrors")
              .invoke(result);

          if (resolvableErrors instanceof List<?> resolvables) {
            for (Object error : resolvables) {
              Object defaultMessage = error.getClass()
                  .getMethod("getDefaultMessage")
                  .invoke(error);
              errors.add(defaultMessage == null ? error.toString() : defaultMessage.toString());
            }
          }
        }

        if (!errors.isEmpty()) {
          return errors;
        }
      }
    } catch (ReflectiveOperationException reflectionException) {
      log.debug("Cannot extract detailed method validation errors", reflectionException);
    }

    return List.of(ex.getMessage());
  }

}
