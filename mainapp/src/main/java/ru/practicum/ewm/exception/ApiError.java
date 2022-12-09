package ru.practicum.ewm.exception;

import lombok.Data;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Data
public class ApiError {
    private List<String> errors = new ArrayList<>();
    private String message;
    private String reason;
    private String status;
    private Timestamp timestamp;

}
