package org.example.tennisscoreboard.mapper;

import org.example.tennisscoreboard.dto.ScoreDTO;
import org.example.tennisscoreboard.entity.Score;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

@Mapper
public interface ScoreMapper {

    ScoreMapper INSTANCE = Mappers.getMapper(ScoreMapper.class);

    @Mappings({
            @Mapping(target = "points", expression = "java(changeToAD(score))")
    })
    ScoreDTO toDTO(Score score);

    @Mappings({
            @Mapping(target = "points", expression = "java(changeFromAD(scoreDTO))")
    })
    Score toObject(ScoreDTO scoreDTO);

    default String changeToAD(Score score) {
        return (score.getPoints() == -1) ? "AD" : score.getPoints() + "";
    }

    default int changeFromAD(ScoreDTO scoreDTO) {
        return scoreDTO.points().equals("AD") ? -1 : Integer.parseInt(scoreDTO.points());
    }
}
