package com.example.badminton.dto;

import com.example.badminton.entity.Player;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class PlayerResponse {
    private Long id;
    private String name;
    private String phone;
    private String email;
    private Boolean active;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static PlayerResponse fromEntity(Player player) {
        PlayerResponse dto = new PlayerResponse();
        dto.setId(player.getId());
        dto.setName(player.getName());
        dto.setPhone(player.getPhone());
        dto.setEmail(player.getEmail());
        dto.setActive(player.getActive());
        dto.setCreatedAt(player.getCreatedAt());
        dto.setUpdatedAt(player.getUpdatedAt());
        return dto;
    }
}
