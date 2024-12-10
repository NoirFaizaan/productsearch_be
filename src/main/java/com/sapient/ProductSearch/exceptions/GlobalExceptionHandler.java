package com.sapient.ProductSearch.exceptions;

import com.sapient.ProductSearch.util.ApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ControllerAdvice;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    // Handle custom ProductNotFoundException
    @ExceptionHandler(ProductNotFoundException.class)
    public ResponseEntity<ApiResponse> handleProductNotFound(ProductNotFoundException ex) {
        logger.error("Product not found: {}", ex.getMessage());
        ApiResponse response = new ApiResponse(ApiResponse.Response.FAILURE, ex.getMessage(),null);
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    // Handle IllegalArgumentException (e.g. invalid query length)
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse> handleIllegalArgumentException(IllegalArgumentException ex) {
        logger.error("Bad request: {}", ex.getMessage());
        ApiResponse response = new ApiResponse(ApiResponse.Response.FAILURE, ex.getMessage(),null);
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    // Handle generic exceptions (e.g. database issues, unknown errors)
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse> handleGenericException(Exception ex) {
        logger.error("Unexpected error: {}", ex.getMessage(), ex);
        ApiResponse response = new ApiResponse(ApiResponse.Response.FAILURE, "An unexpected error occurred.",null);
        return new ResponseEntity<>(response, HttpStatus.NO_CONTENT);
    }

    @ExceptionHandler(InvalidInputException.class)
    public ResponseEntity<ApiResponse> handleInvalidInput(InvalidInputException ex) {
        return new ResponseEntity<>(new ApiResponse(ApiResponse.Response.FAILURE, "Invalid input: " + ex.getMessage(), null), HttpStatus.BAD_REQUEST);
    }
}
