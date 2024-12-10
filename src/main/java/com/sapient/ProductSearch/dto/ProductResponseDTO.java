package com.sapient.ProductSearch.dto;

import lombok.Data;
import java.util.List;

@Data
public class ProductResponseDTO {
    private Long id;
    private String title;
    private String description;
    private String category;
    private Double price;
    private Double discountPercentage;
    private Double rating;
    private Integer stock;
    private String brand;
    private String sku;
    private Double weight;
    private String warrantyInformation;
    private String shippingInformation;
    private String availabilityStatus;
    private List<String> tags;
    private List<ReviewDTO> reviews;
    private DimensionsDTO dimensions;
    private MetaDTO meta;
    private String returnPolicy;
    private Integer minimumOrderQuantity;
    private String thumbnail;
    private List<String> images;
}
