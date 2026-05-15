package org.example.tennisscoreboard.mapper;

import org.example.tennisscoreboard.dto.PointWinnerRequestDTO;
import org.example.tennisscoreboard.entity.PointWinner;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface PointWinnerMapper {
    PointWinnerMapper INSTANCE = Mappers.getMapper(PointWinnerMapper.class);

    PointWinner toObject(PointWinnerRequestDTO pointWinnerRequestDTO);
}
