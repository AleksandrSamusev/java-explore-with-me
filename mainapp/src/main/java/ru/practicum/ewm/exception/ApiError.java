package ru.practicum.ewm.exception;

import lombok.Data;

import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Set;

@Data
public class ApiError {
    private Set<String> errors = new HashSet<>();
    private String message;
    private String reason;
    private String status;
    private Timestamp timestamp;

}
