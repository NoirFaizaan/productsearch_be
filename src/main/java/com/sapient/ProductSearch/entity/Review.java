package com.sapient.ProductSearch.entity;

import java.time.Instant;

import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Rating cannot be null")
    @DecimalMin(value = "0.0", message = "Rating must be between 0 and 5")
    @DecimalMax(value = "5.0", message = "Rating must be between 0 and 5")
    private Double rating;

    @NotBlank(message = "Comment cannot be blank")
    @Size(max = 300, message = "Comment must not exceed 300 characters")
    private String comment;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSX", timezone = "UTC")
    private Instant date;

    @NotBlank(message = "Reviewer name cannot be blank")
    private String reviewerName;

    @NotBlank(message = "Reviewer email cannot be blank")
    @Email(message = "Reviewer email must be valid")
    private String reviewerEmail;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;
}
