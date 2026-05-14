package org.example.tennisscoreboard.mapper;

import org.example.tennisscoreboard.dto.PlayerDTO;
import org.example.tennisscoreboard.entity.Player;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface PlayerMapper {

    PlayerMapper INSTANCE = Mappers.getMapper(PlayerMapper.class);

    PlayerDTO toDTO(Player player);

    Player toObject(PlayerDTO playerDTO);
}
