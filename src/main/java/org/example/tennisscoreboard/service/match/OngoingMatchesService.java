package org.example.tennisscoreboard.service.match;

import org.example.tennisscoreboard.dto.MatchAndScoreResponseDTO;
import org.example.tennisscoreboard.dto.PlayersDTO;
import org.example.tennisscoreboard.entity.*;
import org.example.tennisscoreboard.mapper.MatchAndScoreMapper;
import org.example.tennisscoreboard.mapper.PlayerMapper;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class OngoingMatchesService {
    private final PlayerMapper playerMapper = PlayerMapper.INSTANCE;
    private final MatchAndScoreMapper matchAndScoreMapper = MatchAndScoreMapper.INSTANCE;
    private final Map<UUID, MatchAndScore> ongoingMatches = new HashMap<>();

    public MatchAndScoreResponseDTO registerNewMatch(PlayersDTO playersDTO) {
        Player playerOne = playerMapper.toObject(playersDTO.playerOne());
        Player playerTwo = playerMapper.toObject(playersDTO.playerTwo());

        UUID uuid = UUID.randomUUID();
        MatchAndScore matchAndScore = new MatchAndScore(new Match(playerOne, playerTwo), new GeneralScore(new Score(), new Score()));
        ongoingMatches.put(uuid, matchAndScore);

        return matchAndScoreMapper.toDto(matchAndScore);
    }
}
