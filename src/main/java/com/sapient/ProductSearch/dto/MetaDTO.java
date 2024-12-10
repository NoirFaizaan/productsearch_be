package com.sapient.ProductSearch.dto;

import lombok.Data;
import jakarta.validation.constraints.*;
import java.time.Instant;

@Data
public class MetaDTO {
    @NotBlank
    private String barcode;

    @NotBlank
    private String qrCode;

    @NotNull
    private Instant createdAt;

    @NotNull
    private Instant updatedAt;
}
