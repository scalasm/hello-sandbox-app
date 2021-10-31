package me.marioscalasm.hsa.architecture.web;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;

import org.springframework.context.MessageSource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.marioscalasm.hsa.architecture.exception.ApplicationException;
import me.marioscalasm.hsa.architecture.exception.ResourceNotFoundException;

@Getter
@RequiredArgsConstructor
class StandardErrorResponse {
    private Date date = new Date();

    private final String message;
}

@Getter
class ValidationErrorResponse extends StandardErrorResponse {
    private final List<FieldValidationError> violations = new ArrayList<>();

    public ValidationErrorResponse(String message) {
        super(message);
    }
}

@Getter
@RequiredArgsConstructor(staticName = "of")
class FieldValidationError {
    private final String fieldName;

    private final String message;
}

/**
 * Exception mapping logic: translate application exception to something that
 * external clients may interpret (e.g., specific validation errors for each
 * input field).
 * 
 * See https://reflectoring.io/bean-validation-with-spring-boot/ for more
 * information.
 */
@ControllerAdvice
@RequiredArgsConstructor
public class RestResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {

    private final MessageSource messageSource;

    @ExceptionHandler(value = { ResourceNotFoundException.class })
    protected ResponseEntity<Object> handleResourceNotFoundException(ResourceNotFoundException ex, WebRequest request) {
        final String errorMessage = messageSource.getMessage(ex.getMessage(),
                new Object[] { ex.getResourceClass().getSimpleName(), ex.getResourceId() }, request.getLocale());

        return handleExceptionInternal(ex, new StandardErrorResponse(errorMessage), new HttpHeaders(),
                HttpStatus.NOT_FOUND, request);
    }

    @ExceptionHandler(value = { ApplicationException.class })
    protected ResponseEntity<Object> handleApplicationException(ApplicationException ex, WebRequest request) {
        String errorMessage = messageSource.getMessage(ex.getMessage(), null, request.getLocale());

        return handleExceptionInternal(ex, errorMessage, new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR, request);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    ValidationErrorResponse onConstraintValidationException(ConstraintViolationException e, WebRequest request) {
        final String errorMessage = messageSource.getMessage("exception.validation_failed",
                new Object[] { "<unknown>" }, request.getLocale());

        ValidationErrorResponse error = new ValidationErrorResponse(errorMessage);
        for (ConstraintViolation<?> violation : e.getConstraintViolations()) {
            error.getViolations()
                    .add(FieldValidationError.of(violation.getPropertyPath().toString(), violation.getMessage()));
        }
        return error;
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException e,
            HttpHeaders headers, HttpStatus status, WebRequest request) {
        final String errorMessage = messageSource.getMessage("exception.validation_failed",
                new Object[] { e.getTarget().getClass().getSimpleName() }, request.getLocale());

        ValidationErrorResponse error = new ValidationErrorResponse(errorMessage);
        for (FieldError fieldError : e.getBindingResult().getFieldErrors()) {
            error.getViolations().add(FieldValidationError.of(fieldError.getField(), fieldError.getDefaultMessage()));
        }
        return handleExceptionInternal(e, error, headers, status, request);
    }
}