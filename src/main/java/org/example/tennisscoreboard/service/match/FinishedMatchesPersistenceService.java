package org.example.tennisscoreboard.service.match;

import lombok.RequiredArgsConstructor;
import org.example.tennisscoreboard.common.BaseDAO;
import org.example.tennisscoreboard.dto.MatchDTO;
import org.example.tennisscoreboard.entity.Match;
import org.example.tennisscoreboard.mapper.MatchMapper;

import java.util.List;

@RequiredArgsConstructor
public class FinishedMatchesPersistenceService {
    private final BaseDAO<Match, List<Match>> h2FinishedMatchDAO;
    private final MatchMapper matchMapper = MatchMapper.INSTANCE;

    public void addFinishedMatch(MatchDTO matchDTO) {
        Match match = matchMapper.toObject(matchDTO);
        if (match.getWinner() != null) {
            h2FinishedMatchDAO.insert(match);
        }
    }
}
