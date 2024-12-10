package com.sapient.ProductSearch.dto;

import lombok.Data;
import jakarta.validation.constraints.*;

@Data
public class DimensionsDTO {
    @NotNull
    private Double width;

    @NotNull
    private Double height;

    @NotNull
    private Double depth;
}
