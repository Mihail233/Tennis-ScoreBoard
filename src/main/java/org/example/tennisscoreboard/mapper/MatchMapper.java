package org.example.tennisscoreboard.mapper;

import org.example.tennisscoreboard.dto.MatchDTO;
import org.example.tennisscoreboard.entity.Match;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(uses = PlayerMapper.class)
public interface MatchMapper {
    MatchMapper INSTANCE = Mappers.getMapper(MatchMapper.class);

    MatchDTO toDTO(Match match);
}
