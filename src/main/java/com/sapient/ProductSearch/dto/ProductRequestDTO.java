package com.sapient.ProductSearch.dto;

import lombok.Data;
import jakarta.validation.constraints.*;
import java.util.List;

@Data
public class ProductRequestDTO {
    @NotBlank
    private String title;

    @NotBlank
    private String description;

    @NotBlank
    private String category;

    @NotNull
    private Double price;

    @NotNull
    private Double discountPercentage;

    @NotNull
    private Double rating;

    @NotNull
    private Integer stock;

    private String brand;

    @NotBlank
    private String sku;

    @NotNull
    private Double weight;

    @NotBlank
    private String warrantyInformation;

    @NotBlank
    private String shippingInformation;

    @NotBlank
    private String availabilityStatus;

    private List<String> tags;

    private List<ReviewDTO> reviews;

    private DimensionsDTO dimensions;

    private MetaDTO meta;

    @NotBlank
    private String returnPolicy;

    @NotNull
    private Integer minimumOrderQuantity;

    @NotBlank
    private String thumbnail;

    private List<String> images;
}
