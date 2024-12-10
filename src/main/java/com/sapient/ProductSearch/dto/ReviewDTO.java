package com.sapient.ProductSearch.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.*;
import java.time.Instant;

@Data
@AllArgsConstructor
@NoArgsConstructor
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

