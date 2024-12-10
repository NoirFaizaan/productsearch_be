package com.sapient.ProductSearch.exceptions;

public class InvalidInputException extends IllegalArgumentException{
    public InvalidInputException(String message) {
        super(message);
    }
}
