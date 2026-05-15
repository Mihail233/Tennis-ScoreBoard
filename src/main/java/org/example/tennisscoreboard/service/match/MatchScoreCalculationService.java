package org.example.tennisscoreboard.service.match;

import org.example.tennisscoreboard.dto.MatchAndScoreResponseDTO;
import org.example.tennisscoreboard.dto.PointWinnerRequestDTO;
import org.example.tennisscoreboard.entity.*;
import org.example.tennisscoreboard.mapper.MatchAndScoreMapper;
import org.example.tennisscoreboard.mapper.PointWinnerMapper;

public class MatchScoreCalculationService {
    private static final int WIN_SETS_IN_ONGOING_MATCH = 2;
    private static final int GAMES_IN_SET = 6;
    private static final int POINTS_IN_TIEBREAK = 7;
    private static final int WIN_MARGIN = 2;
    private static final int AD = -1;
    private static final int RESET = -2;

    private static final int ZERO_POINT = 0;
    private static final int ONE_POINT = 15;
    private static final int TWO_POINT = 30;
    private static final int THREE_POINT = 40;

    private final PointWinnerMapper pointWinnerMapper = PointWinnerMapper.INSTANCE;
    private final MatchAndScoreMapper matchAndScoreMapper = MatchAndScoreMapper.INSTANCE;

    public MatchAndScoreResponseDTO updateMatchScore(PointWinnerRequestDTO pointWinnerRequestDTO, MatchAndScoreResponseDTO matchAndScoreResponseDTO) {

        PointWinner pointWinner = pointWinnerMapper.toObject(pointWinnerRequestDTO);
        MatchAndScore matchAndScore = matchAndScoreMapper.toObject(matchAndScoreResponseDTO);

        Long pointWinnerId = Long.valueOf(pointWinner.getPointWinnerId());
        Match match = matchAndScore.getMatch();
        Long playerOneId = match.getPlayerOne().getId();
        Score playerOneScore = matchAndScore.getGeneralScore().getPlayerOneScore();
        Score playerTwoScore = matchAndScore.getGeneralScore().getPlayerTwoScore();

        if (playerOneId.equals(pointWinnerId)) {
            updatePoint(playerOneScore, playerTwoScore);
            updateGame(playerOneScore, playerTwoScore);
            updateSet(playerOneScore, playerTwoScore);
            updateWinner(match.getPlayerOne(), playerOneScore, matchAndScore);

        } else {
            updatePoint(playerTwoScore, playerOneScore);
            updateGame(playerTwoScore, playerOneScore);
            updateSet(playerTwoScore, playerOneScore);
            updateWinner(match.getPlayerTwo(), playerTwoScore, matchAndScore);
        }

        return matchAndScoreMapper.toDto(matchAndScore);
    }

    private void updatePoint(Score winnerScore, Score loserScore) {
        int winnerGames = winnerScore.getGames();
        int loserGames = loserScore.getGames();

        if (hasTiebreak(winnerGames, loserGames)) {
            updatePointInTiebreak(winnerScore, loserScore);

        } else {
            updatePointInStandardGame(winnerScore, loserScore);
        }

        resetPoints(winnerScore, loserScore);
    }

    private boolean hasTiebreak(int winnerGames, int loserGames) {
        return winnerGames == GAMES_IN_SET && loserGames == GAMES_IN_SET;
    }

    private void updatePointInTiebreak(Score winnerScore, Score loserScore) {
        //7 win + разница в очка, если нет то играют дальше
        int winnerPoints = winnerScore.getPoints();

        winnerPoints++;
        winnerScore.setPoints(winnerPoints);
        findWinnerInTiebreak(winnerScore, loserScore);
    }

    private void findWinnerInTiebreak(Score winnerScore, Score loserScore) {

        int highestPoints = Math.max(winnerScore.getPoints(), loserScore.getPoints());
        int lowestPoints = Math.min(winnerScore.getPoints(), loserScore.getPoints());

        boolean hasWinnerInTieBreak = (highestPoints >= POINTS_IN_TIEBREAK && (highestPoints - lowestPoints >= WIN_MARGIN));

        if (hasWinnerInTieBreak) {
            winnerScore.setPoints(RESET);
        }
    }

    //AD - кидать в DTO, а здесь реализовать по рамках числового формата
    private void updatePointInStandardGame(Score winnerScore, Score loserScore) {

        boolean hasTieScore = hasTieScore(winnerScore, loserScore);
        boolean hasAdvantage = hasAdvantage(winnerScore, loserScore);

        updatePointInDifferentStage(hasTieScore, hasAdvantage, winnerScore, loserScore);
    }

    private void updatePointInDifferentStage(boolean hasTieScore, boolean hasAdvantage, Score winnerScore, Score loserScore) {
        //AD
        if (hasTieScore) {
            winnerScore.setPoints(AD);

        } else if (hasAdvantage) {

            if (winnerScore.getPoints() == AD) {
                winnerScore.setPoints(RESET);
            } else {
                loserScore.setPoints(THREE_POINT);
            }

        } else {
            addPoint(winnerScore);
        }
    }

    private boolean hasTieScore(Score winnerScore, Score loserScore) {
        return winnerScore.getPoints() == THREE_POINT && loserScore.getPoints() == THREE_POINT;
    }

    private boolean hasAdvantage(Score winnerScore, Score loserScore) {
        return winnerScore.getPoints() == AD || loserScore.getPoints() == AD;
    }

    private void addPoint(Score score) {
        int points = score.getPoints();

        switch (points) {
            case (ZERO_POINT):
                score.setPoints(ONE_POINT);
                break;
            case (ONE_POINT):
                score.setPoints(TWO_POINT);
                break;
            case (TWO_POINT):
                score.setPoints(THREE_POINT);
                break;
            case (THREE_POINT):
                score.setPoints(RESET);
                break;
            default:
                throw new IllegalArgumentException("Unexpected value");
        }
    }

    private void resetPoints(Score winnerScore, Score loserScore) {
        int playerOnePoints = winnerScore.getPoints();
        int loserPoints = loserScore.getPoints();

        if (hasResetPoints(playerOnePoints) || hasResetPoints(loserPoints)) {
            winnerScore.setPoints(ZERO_POINT);
            loserScore.setPoints(ZERO_POINT);
        }
    }

    private boolean hasResetPoints(int points) {
        return points == RESET;
    }

    private void updateGame(Score winnerScore, Score loserScore) {
        //от pointWinner зависит кому дать game

        int winnerPoints = winnerScore.getPoints();
        int loserPoints = loserScore.getPoints();

        int winnerGames = winnerScore.getGames();

        if (hasEndedGame(winnerPoints, loserPoints)) {
            winnerGames++;
            winnerScore.setGames(winnerGames);
        }

        resetGames(winnerScore, loserScore);
    }

    private boolean hasEndedGame(int winnerPoints, int loserPoints) {
        return winnerPoints == ZERO_POINT && loserPoints == ZERO_POINT;
    }

    private void resetGames(Score winnerScore, Score loserScore) {
        if (hasWinningSet(winnerScore, loserScore)) {
            winnerScore.setGames(ZERO_POINT);
            loserScore.setGames(ZERO_POINT);
        }
    }

    private boolean hasWinningSet(Score winnerScore, Score loserScore) {
        // max >=  GAMES_IN_SET  и max - min -> меньше на 2 очка

        int highestGames = Math.max(winnerScore.getGames(), loserScore.getGames());
        int lowestGames = Math.min(winnerScore.getGames(), loserScore.getGames());

        boolean hasDefaultWinningSet = highestGames >= GAMES_IN_SET && (highestGames - lowestGames >= WIN_MARGIN);
        boolean hasTiebreakWinningSet = highestGames > GAMES_IN_SET && lowestGames == GAMES_IN_SET;

        return hasDefaultWinningSet || hasTiebreakWinningSet;
    }

    private void updateSet(Score winnerScore, Score loserScore) {

        int winnerPoints = winnerScore.getPoints();
        int loserPoints = loserScore.getPoints();

        int winnerGames = winnerScore.getGames();
        int loserGames = loserScore.getGames();

        int winnerSets = winnerScore.getSets();

        if (hasEndedSet(winnerPoints, loserPoints, winnerGames, loserGames)) {
            winnerSets++;
            winnerScore.setSets(winnerSets);
        }
    }

    private boolean hasEndedSet(int winnerPoints, int loserPoints, int winnerGames, int loserGames) {
        return winnerPoints == ZERO_POINT && loserPoints == ZERO_POINT && winnerGames == ZERO_POINT && loserGames == ZERO_POINT;
    }

    private void updateWinner(Player winner, Score winnerScore, MatchAndScore matchAndScore) {
        int winnerSets = winnerScore.getSets();

        if (winnerSets == WIN_SETS_IN_ONGOING_MATCH) {
            matchAndScore.getMatch().setWinner(winner);
        }
    }
}
