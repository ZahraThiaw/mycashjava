package sn.odc.flutter.Web.Dtos.response;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Generic API Response class that wraps all API responses
 * @param <T> Type of data being returned
 */
public class GenericResponse<T> {
    private boolean success;
    private String message;
    private T data;
    private List<String> errors;
    private Long timestamp;
    private String status;
    private Integer statusCode;

    /**
     * Default constructor
     */
    public GenericResponse() {
        this.timestamp = System.currentTimeMillis();
        this.errors = new ArrayList<>();
    }

    /**
     * Constructor for successful responses
     * @param data The payload
     * @param message Success message
     */
    public GenericResponse(T data, String message) {
        this();
        this.success = true;
        this.data = data;
        this.message = message; // Correction de this.message = this.message
        this.status = "SUCCESS";
        this.statusCode = 200;
    }

    /**
     * Constructor for error responses
     * @param message Error message
     */
    public GenericResponse(String message) {
        this();
        this.success = false;
        this.message = Objects.requireNonNull(message, "Message cannot be null");
        this.status = "ERROR";
        this.statusCode = 400;
    }

    // Getters and Setters
    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = Objects.requireNonNull(message, "Message cannot be null");
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public List<String> getErrors() {
        if (errors == null) {
            errors = new ArrayList<>();
        }
        return errors;
    }

    public void setErrors(List<String> errors) {
        this.errors = errors;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = Objects.requireNonNull(timestamp, "Timestamp cannot be null");
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = Objects.requireNonNull(status, "Status cannot be null");
    }

    public Integer getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(Integer statusCode) {
        this.statusCode = Objects.requireNonNull(statusCode, "Status code cannot be null");
    }

    /**
     * Add a single error to the errors list
     * @param error Error message to add
     */
    public void addError(String error) {
        getErrors().add(error);
    }
}