package ru.practicum.ewm.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.Arrays;
import java.util.Collections;

@RestControllerAdvice
public class ErrorHandler {

    StringWriter sw = new StringWriter();
    PrintWriter pw = new PrintWriter(sw);
    ApiError apiError = new ApiError();

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiError handleUserNotFoundException(final UserNotFoundException e) {
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
        e.printStackTrace(pw);
        String stackTrace = sw.toString();
        apiError.setErrors(Collections.singletonList(stackTrace));
        apiError.setStatus(HttpStatus.CONFLICT.name());
        apiError.setMessage(e.getMessage());
        apiError.setReason("Conflict");
        apiError.setTimestamp(Timestamp.from(Instant.now()));
        return apiError;
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiError handleUserConflictException(final UserConflictException e) {
        e.printStackTrace(pw);
        String stackTrace = sw.toString();
        apiError.setErrors(Collections.singletonList(stackTrace));
        apiError.setStatus(HttpStatus.CONFLICT.name());
        apiError.setMessage(e.getMessage());
        apiError.setReason("Conflict");
        apiError.setTimestamp(Timestamp.from(Instant.now()));
        return apiError;
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiError handleCategoryNotFoundException(final CategoryNotFoundException e) {
        e.printStackTrace(pw);
        String stackTrace = sw.toString();
        apiError.setErrors(Collections.singletonList(stackTrace));
        apiError.setStatus(HttpStatus.NOT_FOUND.name());
        apiError.setMessage(e.getMessage());
        apiError.setReason("Not found");
        apiError.setTimestamp(Timestamp.from(Instant.now()));
        return apiError;
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiError handleEventNotFoundException(final EventNotFoundException e) {
        e.printStackTrace(pw);
        String stackTrace = sw.toString();
        apiError.setErrors(Collections.singletonList(stackTrace));
        apiError.setStatus(HttpStatus.NOT_FOUND.name());
        apiError.setMessage(e.getMessage());
        apiError.setReason("Not found");
        apiError.setTimestamp(Timestamp.from(Instant.now()));
        return apiError;
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiError handleRequestNotFoundException(final RequestNotFoundException e) {
        e.printStackTrace(pw);
        String stackTrace = sw.toString();
        apiError.setErrors(Collections.singletonList(stackTrace));
        apiError.setStatus(HttpStatus.NOT_FOUND.name());
        apiError.setMessage(e.getMessage());
        apiError.setReason("Not found");
        apiError.setTimestamp(Timestamp.from(Instant.now()));
        return apiError;
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiError handleCompilationNotFoundException(final CompilationNotFoundException e) {
        e.printStackTrace(pw);
        String stackTrace = sw.toString();
        apiError.setErrors(Collections.singletonList(stackTrace));
        apiError.setStatus(HttpStatus.NOT_FOUND.name());
        apiError.setMessage(e.getMessage());
        apiError.setReason("Not found");
        apiError.setTimestamp(Timestamp.from(Instant.now()));
        return apiError;
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleInvalidParameterException(final InvalidParameterException e) {
        e.printStackTrace(pw);
        String stackTrace = sw.toString();
        apiError.setErrors(Collections.singletonList(stackTrace));
        apiError.setStatus(HttpStatus.BAD_REQUEST.name());
        apiError.setMessage(e.getMessage());
        apiError.setReason("Bad request");
        apiError.setTimestamp(Timestamp.from(Instant.now()));
        return apiError;
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiError handleReviewNotFoundException(final ReviewNotFoundException e) {
        e.printStackTrace(pw);
        String stackTrace = sw.toString();
        apiError.setErrors(Collections.singletonList(stackTrace));
        apiError.setStatus(HttpStatus.NOT_FOUND.name());
        apiError.setMessage(e.getMessage());
        apiError.setReason("Not found");
        apiError.setTimestamp(Timestamp.from(Instant.now()));
        return apiError;
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ApiError handleForbiddenException(final ForbiddenException e) {
        e.printStackTrace(pw);
        String stackTrace = sw.toString();
        apiError.setErrors(Collections.singletonList(stackTrace));
        apiError.setStatus(HttpStatus.FORBIDDEN.name());
        apiError.setMessage(e.getMessage());
        apiError.setReason("Forbidden");
        apiError.setTimestamp(Timestamp.from(Instant.now()));
        return apiError;
    }

}
