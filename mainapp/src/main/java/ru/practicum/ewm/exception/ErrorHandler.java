package ru.practicum.ewm.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.Arrays;

@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiError handleUserNotFoundException(final UserNotFoundException e) {
        ApiError apiError = new ApiError();
        Arrays.stream(e.getStackTrace())
                .forEach(er -> apiError.getErrors().add(er.toString()));
        apiError.setStatus(HttpStatus.NOT_FOUND.name());
        apiError.setMessage(e.getMessage());
        apiError.setReason("Not found");
        apiError.setTimestamp(Timestamp.from(Instant.now()));
        return apiError;
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiError handleCategoryConflictException(final CategoryConflictException e) {
        ApiError apiError = new ApiError();
        Arrays.stream(e.getStackTrace())
                .forEach(er -> apiError.getErrors().add(er.toString()));
        apiError.setStatus(HttpStatus.CONFLICT.name());
        apiError.setMessage(e.getMessage());
        apiError.setReason("Conflict");
        apiError.setTimestamp(Timestamp.from(Instant.now()));
        return apiError;
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiError handleUserConflictException(final UserConflictException e) {
        ApiError apiError = new ApiError();
        Arrays.stream(e.getStackTrace())
                .forEach(er -> apiError.getErrors().add(er.toString()));
        apiError.setStatus(HttpStatus.CONFLICT.name());
        apiError.setMessage(e.getMessage());
        apiError.setReason("Conflict");
        apiError.setTimestamp(Timestamp.from(Instant.now()));
        return apiError;
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiError handleCategoryNotFoundException(final CategoryNotFoundException e) {
        ApiError apiError = new ApiError();
        Arrays.stream(e.getStackTrace())
                .forEach(er -> apiError.getErrors().add(er.toString()));
        apiError.setStatus(HttpStatus.NOT_FOUND.name());
        apiError.setMessage(e.getMessage());
        apiError.setReason("Not found");
        apiError.setTimestamp(Timestamp.from(Instant.now()));
        return apiError;
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiError handleEventNotFoundException(final EventNotFoundException e) {
        ApiError apiError = new ApiError();
        Arrays.stream(e.getStackTrace())
                .forEach(er -> apiError.getErrors().add(er.toString()));
        apiError.setStatus(HttpStatus.NOT_FOUND.name());
        apiError.setMessage(e.getMessage());
        apiError.setReason("Not found");
        apiError.setTimestamp(Timestamp.from(Instant.now()));
        return apiError;
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiError handleRequestNotFoundException(final RequestNotFoundException e) {
        ApiError apiError = new ApiError();
        Arrays.stream(e.getStackTrace())
                .forEach(er -> apiError.getErrors().add(er.toString()));
        apiError.setStatus(HttpStatus.NOT_FOUND.name());
        apiError.setMessage(e.getMessage());
        apiError.setReason("Not found");
        apiError.setTimestamp(Timestamp.from(Instant.now()));
        return apiError;
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiError handleCompilationNotFoundException(final CompilationNotFoundException e) {
        ApiError apiError = new ApiError();
        Arrays.stream(e.getStackTrace())
                .forEach(er -> apiError.getErrors().add(er.toString()));
        apiError.setStatus(HttpStatus.NOT_FOUND.name());
        apiError.setMessage(e.getMessage());
        apiError.setReason("Not found");
        apiError.setTimestamp(Timestamp.from(Instant.now()));
        return apiError;
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleInvalidParameterException(final InvalidParameterException e) {
        ApiError apiError = new ApiError();
        Arrays.stream(e.getStackTrace())
                .forEach(er -> apiError.getErrors().add(er.toString()));
        apiError.setStatus(HttpStatus.BAD_REQUEST.name());
        apiError.setMessage(e.getMessage());
        apiError.setReason("Bad request");
        apiError.setTimestamp(Timestamp.from(Instant.now()));
        return apiError;
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiError handleReviewNotFoundException(final ReviewNotFoundException e) {
        ApiError apiError = new ApiError();
        Arrays.stream(e.getStackTrace())
                .forEach(er -> apiError.getErrors().add(er.toString()));
        apiError.setStatus(HttpStatus.NOT_FOUND.name());
        apiError.setMessage(e.getMessage());
        apiError.setReason("Not found");
        apiError.setTimestamp(Timestamp.from(Instant.now()));
        return apiError;
    }
}
