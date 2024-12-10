package com.sapient.ProductSearch.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.*;
import java.time.Instant;

@Data
@AllArgsConstructor
@NoArgsConstructor
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
