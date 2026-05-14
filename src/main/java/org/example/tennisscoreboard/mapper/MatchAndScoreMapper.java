package org.example.tennisscoreboard.mapper;

import org.example.tennisscoreboard.dto.MatchAndScoreResponseDTO;
import org.example.tennisscoreboard.entity.MatchAndScore;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(uses = {MatchMapper.class, GeneralScoreMapper.class, ScoreMapper.class})
public interface MatchAndScoreMapper {
    MatchAndScoreMapper INSTANCE = Mappers.getMapper(MatchAndScoreMapper.class);

    MatchAndScoreResponseDTO toDto(MatchAndScore matchAndScore);
}
