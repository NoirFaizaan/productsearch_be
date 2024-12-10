package com.sapient.ProductSearch.util;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ApiResponse {
    private Response response;
    private String message;
    private Object data;

    public enum Response {
        SUCCESS, FAILURE
    }
}