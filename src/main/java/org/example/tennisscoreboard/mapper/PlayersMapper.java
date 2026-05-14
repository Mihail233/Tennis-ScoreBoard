package org.example.tennisscoreboard.mapper;

import org.example.tennisscoreboard.dto.PlayersDTO;
import org.example.tennisscoreboard.entity.Player;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(uses = PlayerMapper.class)
public interface PlayersMapper {
    PlayersMapper INSTANCE = Mappers.getMapper(PlayersMapper.class);

    PlayersDTO toDTO(Player playerOne, Player playerTwo);
}
