package org.example.tennisscoreboard.mapper;

import org.example.tennisscoreboard.dto.GeneralScoreDTO;
import org.example.tennisscoreboard.entity.Score;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(uses = Score.class)
public interface GeneralScoreMapper {
    GeneralScoreMapper INSTANCE = Mappers.getMapper(GeneralScoreMapper.class);

    GeneralScoreDTO toDTO(Score playerOneScore, Score playerTwoScore);
}
