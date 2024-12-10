package com.sapient.ProductSearch.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DimensionsDTO {
    @NotNull
    private Double width;

    @NotNull
    private Double height;

    @NotNull
    private Double depth;
}
