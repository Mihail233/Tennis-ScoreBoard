package org.example.tennisscoreboard.service.player;

import lombok.RequiredArgsConstructor;
import org.example.tennisscoreboard.entity.Player;
import org.example.tennisscoreboard.common.BaseDAO;
import org.example.tennisscoreboard.dto.PlayersRequestDTO;
import org.example.tennisscoreboard.dto.PlayersDTO;
import org.example.tennisscoreboard.mapper.PlayersMapper;

@RequiredArgsConstructor
public class PlayerService {
    private final PlayersMapper playersMapper = PlayersMapper.INSTANCE;
    private final BaseDAO<Player, Player> h2PlayerDAO;

    //если 2 пользователя исп app параллельно,
    // у первого при select будет null, второй пользователь вставит в этот момент player,
    // первый попытается вставить и у него будет exception

    public PlayersDTO findPlayers(PlayersRequestDTO playersRequestDTO) {

        String playerOneName = playersRequestDTO.playerOneName();
        String playerTwoName = playersRequestDTO.playerTwoName();

        Player playerOne = new Player(playerOneName);
        Player playerTwo = new Player(playerTwoName);

        h2PlayerDAO.insert(playerOne);
        h2PlayerDAO.insert(playerTwo);

        playerOne = h2PlayerDAO.findByName(playerOneName);
        playerTwo = h2PlayerDAO.findByName(playerTwoName);

        return playersMapper.toDTO(playerOne, playerTwo);
    }
}
