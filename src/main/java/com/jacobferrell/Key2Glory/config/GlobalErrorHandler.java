package com.jacobferrell.Key2Glory.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.server.ResponseStatusException;

import com.jacobferrell.Key2Glory.model.ErrorMessage;

import java.util.NoSuchElementException;

@RestControllerAdvice
public class GlobalErrorHandler {
    private static final Logger log = LoggerFactory.getLogger(GlobalErrorHandler.class);

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ErrorMessage> handleResponseStatus(final ResponseStatusException error) {
        String reason = error.getReason() != null ? error.getReason() : error.getStatusCode().toString();
        return ResponseEntity.status(error.getStatusCode()).body(ErrorMessage.from(reason));
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(NoSuchElementException.class)
    public ErrorMessage handleNotFound() {
        return ErrorMessage.from("Not Found");
    }

    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ExceptionHandler(AccessDeniedException.class)
    public ErrorMessage handleAccessDenied() {
        return ErrorMessage.from("Permission denied");
    }

    // Bad enum values, malformed JSON, invalid page requests, etc.
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler({
            IllegalArgumentException.class,
            MethodArgumentTypeMismatchException.class,
            HttpMessageNotReadableException.class
    })
    public ErrorMessage handleBadRequest(final Exception error) {
        if (error instanceof MethodArgumentTypeMismatchException mismatch) {
            return ErrorMessage.from("Invalid value for parameter '" + mismatch.getName() + "'");
        }
        if (error instanceof HttpMessageNotReadableException) {
            return ErrorMessage.from("Malformed request body");
        }
        return ErrorMessage.from(error.getMessage());
    }

    @ExceptionHandler(Throwable.class)
    public ResponseEntity<ErrorMessage> handleInternalError(final Throwable error) {
        // Standard Spring MVC exceptions (405, 415, missing params...) carry their own status
        if (error instanceof ErrorResponse errorResponse) {
            String detail = errorResponse.getBody().getDetail();
            return ResponseEntity.status(errorResponse.getStatusCode())
                    .body(ErrorMessage.from(detail != null ? detail : errorResponse.getStatusCode().toString()));
        }
        log.error("Unhandled exception", error);
        // Don't leak internals to the client
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ErrorMessage.from("Internal server error"));
    }
}
