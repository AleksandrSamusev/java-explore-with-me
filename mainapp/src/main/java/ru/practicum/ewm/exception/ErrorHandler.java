package ru.practicum.ewm.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.Collections;

@Slf4j
@RestControllerAdvice
public class ErrorHandler {

    StringWriter sw = new StringWriter();
    PrintWriter pw = new PrintWriter(sw);
    ApiError apiError = new ApiError();

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiError handleNotFoundException(final NotFoundException e) {
        log.info("404 {}", e.getMessage(), e);
        e.printStackTrace(pw);
        String stackTrace = sw.toString();
        apiError.setErrors(Collections.singletonList(stackTrace));
        apiError.setStatus(HttpStatus.NOT_FOUND.name());
        apiError.setMessage(e.getLocalizedMessage());
        apiError.setReason("Not found");
        apiError.setTimestamp(Timestamp.from(Instant.now()));
        return apiError;
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiError handleConflictException(final ConflictException e) {
        log.info("409 {}", e.getMessage(), e);
        e.printStackTrace(pw);
        String stackTrace = sw.toString();
        apiError.setErrors(Collections.singletonList(stackTrace));
        apiError.setStatus(HttpStatus.CONFLICT.name());
        apiError.setMessage(e.getLocalizedMessage());
        apiError.setReason("Conflict");
        apiError.setTimestamp(Timestamp.from(Instant.now()));
        return apiError;
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleInvalidParameterException(final InvalidParameterException e) {
        log.info("400 {}", e.getMessage(), e);
        e.printStackTrace(pw);
        String stackTrace = sw.toString();
        apiError.setErrors(Collections.singletonList(stackTrace));
        apiError.setStatus(HttpStatus.BAD_REQUEST.name());
        apiError.setMessage(e.getLocalizedMessage());
        apiError.setReason("Bad request");
        apiError.setTimestamp(Timestamp.from(Instant.now()));
        return apiError;
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ApiError handleForbiddenException(final ForbiddenException e) {
        log.info("403 {}", e.getMessage(), e);
        e.printStackTrace(pw);
        String stackTrace = sw.toString();
        apiError.setErrors(Collections.singletonList(stackTrace));
        apiError.setStatus(HttpStatus.FORBIDDEN.name());
        apiError.setMessage(e.getLocalizedMessage());
        apiError.setReason("Forbidden");
        apiError.setTimestamp(Timestamp.from(Instant.now()));
        return apiError;
    }
}
