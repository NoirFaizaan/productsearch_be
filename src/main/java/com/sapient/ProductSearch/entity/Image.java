package com.sapient.ProductSearch.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Image {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Image URL cannot be blank")
    @Pattern(regexp = "^(https?://.*)$", message = "Image URL must be valid")
    private String url;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;
}