package com.sapient.ProductSearch.dto;

import lombok.Data;
import jakarta.validation.constraints.*;
import java.time.Instant;

@Data
public class ReviewDTO {
    @NotNull
    private Double rating;

    @NotBlank
    private String comment;

    @NotNull
    private Instant date;

    @NotBlank
    private String reviewerName;

    @NotBlank
    @Email
    private String reviewerEmail;
}

