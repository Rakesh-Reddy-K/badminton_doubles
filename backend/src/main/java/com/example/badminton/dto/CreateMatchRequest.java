package com.example.badminton.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CreateMatchRequest {
    @NotNull(message = "Played datetime is required")
    private java.time.LocalDateTime playedAt;

    @NotNull(message = "Side A player 1 is required")
    private Long sideAPlayer1Id;

    @NotNull(message = "Side A player 2 is required")
    private Long sideAPlayer2Id;

    @NotNull(message = "Side B player 1 is required")
    private Long sideBPlayer1Id;

    @NotNull(message = "Side B player 2 is required")
    private Long sideBPlayer2Id;

    @NotNull(message = "Side A score is required")
    private Integer sideAScore;

    @NotNull(message = "Side B score is required")
    private Integer sideBScore;

    @Size(max = 5000, message = "Notes must be at most 5000 characters")
    private String notes;
}
