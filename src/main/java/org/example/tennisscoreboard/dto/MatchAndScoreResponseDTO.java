package org.example.tennisscoreboard.dto;

//убрал id, winner которые есть в entity. Они всегда null -> ведь мы не храним текущие матчи, раскрывают DAO слой
public record MatchAndScoreResponseDTO(MatchDTO match, GeneralScoreDTO generalScore) { }
