package org.example.tennisscoreboard.dto;

//идея в том что winner == null, говорит что будет победитель, но он не определен во время матча
public record MatchDTO(PlayerDTO playerOne, PlayerDTO playerTwo, PlayerDTO winner) {
}
