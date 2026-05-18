package org.example.tennisscoreboard.service.match;

import org.example.tennisscoreboard.dto.MatchAndScoreResponseDTO;
import org.example.tennisscoreboard.dto.PlayerDTO;
import org.example.tennisscoreboard.dto.PlayersDTO;
import org.example.tennisscoreboard.entity.*;
import org.example.tennisscoreboard.exception.TestException;
import org.example.tennisscoreboard.mapper.MatchAndScoreMapper;
import org.example.tennisscoreboard.mapper.PlayerMapper;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class OngoingMatchesService {
    private final PlayerMapper playerMapper = PlayerMapper.INSTANCE;
    private final MatchAndScoreMapper matchAndScoreMapper = MatchAndScoreMapper.INSTANCE;
    private final Map<UUID, MatchAndScore> ongoingMatches = new HashMap<>();

    public MatchAndScoreResponseDTO addNewMatch(PlayersDTO playersDTO) {
        Player playerOne = playerMapper.toObject(playersDTO.playerOne());
        Player playerTwo = playerMapper.toObject(playersDTO.playerTwo());

        //UUID uuid = UUID.randomUUID();
        UUID uuid = UUID.fromString("f609a413-255a-4eae-925a-dedddd67e470");

        MatchAndScore matchAndScore = new MatchAndScore(new Match(playerOne, playerTwo), new GeneralScore(new Score(), new Score()));
        ongoingMatches.put(uuid, matchAndScore);

        return matchAndScoreMapper.toDto(matchAndScore);
    }

    public MatchAndScoreResponseDTO getOngoingMatch(String uuid) {
        MatchAndScore matchAndScore = ongoingMatches.get(UUID.fromString(uuid));

        if (matchAndScore == null) {
            throw new TestException("матч не найден");
        }

        return matchAndScoreMapper.toDto(matchAndScore);
    }

    public void saveScore(String uuid, MatchAndScoreResponseDTO matchAndScoreResponseDTO) {
        MatchAndScore matchAndScore = matchAndScoreMapper.toObject(matchAndScoreResponseDTO);
        ongoingMatches.put(UUID.fromString(uuid), matchAndScore);
    }

    public void deleteFinishedMatch(String uuid, PlayerDTO winnerDTO) {
        Player winner = playerMapper.toObject(winnerDTO);
        if (winner != null) {
            ongoingMatches.remove(UUID.fromString(uuid));
        }
    }
}
