package com.sapient.ProductSearch.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor

public class Dimensions {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Width cannot be null")
    @DecimalMin(value = "0.0", inclusive = false, message = "Width must be greater than 0")
    private Double width;

    @NotNull(message = "Height cannot be null")
    @DecimalMin(value = "0.0", inclusive = false, message = "Height must be greater than 0")
    private Double height;

    @NotNull(message = "Depth cannot be null")
    @DecimalMin(value = "0.0", inclusive = false, message = "Depth must be greater than 0")
    private Double depth;

    @OneToOne
    @JoinColumn(name = "product_id")
    private Product product;
}