package org.example.tennisscoreboard.mapper;

import org.example.tennisscoreboard.dto.ScoreDTO;
import org.example.tennisscoreboard.entity.Score;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface ScoreMapper {
    ScoreMapper INSTANCE = Mappers.getMapper(ScoreMapper.class);

    ScoreDTO toDTO(Score score);
}
