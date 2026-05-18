package org.example.tennisscoreboard;

import org.example.tennisscoreboard.dto.*;
import org.example.tennisscoreboard.entity.*;
import org.example.tennisscoreboard.mapper.*;

public class Main {
    public static void main(String[] args) {
        Player playerOne = new Player(4L, "test");
        Player playerTwo = new Player(5L, "ewq");
//        PlayerDTO playerDTO = PlayerMapper.INSTANCE.toDTO(player);
//        PlayersDTO playersDTO = PlayersMapper.INSTANCE.toDTO(new Player(1L, "test"), new Player(2L, "TEST2"));

        Score playerOneScore = new Score(-1, 3, 4);
        Score playerTwoScore = new Score(1, -1, 3);

        MatchAndScore matchAndScore = new MatchAndScore(new Match(playerOne, playerTwo), new GeneralScore(playerOneScore, playerTwoScore));

        MatchAndScoreResponseDTO matchAndScoreResponseDTO = MatchAndScoreMapper.INSTANCE.toDto(matchAndScore);
        MatchAndScore matchAndScore1 = MatchAndScoreMapper.INSTANCE.toObject(matchAndScoreResponseDTO);
        int i = 0 ;
    }
}
