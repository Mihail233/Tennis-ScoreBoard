package org.example.tennisscoreboard.dto;

public record MatchDTO(PlayerDTO playerOne, PlayerDTO playerTwo, PlayerDTO winner) {
}
