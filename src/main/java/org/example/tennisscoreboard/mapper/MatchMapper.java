package org.example.tennisscoreboard.mapper;

import org.example.tennisscoreboard.dto.MatchDTO;
import org.example.tennisscoreboard.entity.Match;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.factory.Mappers;

@Mapper(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE, uses = PlayerMapper.class)
public interface MatchMapper {
    MatchMapper INSTANCE = Mappers.getMapper(MatchMapper.class);

    MatchDTO toDTO(Match match);

    Match toObject(MatchDTO matchDTO);
}
