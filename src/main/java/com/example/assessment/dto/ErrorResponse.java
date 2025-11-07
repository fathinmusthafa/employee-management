package com.example.assessment.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;


public class ErrorResponse {
    private String timestamp;
    private int status;
    private String error;
    private String message;
    private String errorCode;
    private String path;
    private Map<String, String> details;

    // Constructor
    public ErrorResponse(HttpStatus status, String message, String errorCode, String path) {
        this.timestamp = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        this.status = status.value();
        this.error = status.getReasonPhrase();
        this.message = message;
        this.errorCode = errorCode;
        this.path = path;
        this.details = new HashMap<>();
    }

    // Builder method
    public static ErrorResponse of(HttpStatus status, String message, String errorCode, String path) {
        return new ErrorResponse(status, message, errorCode, path);
    }

    // Add detail method
    public ErrorResponse addDetail(String key, String value) {
        this.details.put(key, value);
        return this;
    }

    // Getters and Setters
    public String getTimestamp() { return timestamp; }
    public int getStatus() { return status; }
    public String getError() { return error; }
    public String getMessage() { return message; }
    public String getErrorCode() { return errorCode; }
    public String getPath() { return path; }
    public Map<String, String> getDetails() { return details; }

}
