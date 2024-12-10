package com.sapient.ProductSearch.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import com.fasterxml.jackson.annotation.JsonFormat;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor

public class Meta {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Barcode cannot be blank")
    private String barcode;

    @NotBlank(message = "QR Code URL cannot be blank")
    @Pattern(regexp = "^(https?://.*)$", message = "QR Code must be a valid URL")
    private String qrCode;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSX", timezone = "UTC")
    private Instant createdAt;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSX", timezone = "UTC")
    private Instant updatedAt;

    @OneToOne
    @JoinColumn(name = "product_id")
    private Product product;
}