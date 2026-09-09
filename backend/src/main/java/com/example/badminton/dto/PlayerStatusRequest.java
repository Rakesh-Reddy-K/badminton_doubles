package com.example.badminton.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class PlayerStatusRequest {
    @NotNull(message = "Active status is required")
    private Boolean active;
}
