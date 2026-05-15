package org.example.tennisscoreboard.service.match;

import org.example.tennisscoreboard.dto.MatchAndScoreResponseDTO;
import org.example.tennisscoreboard.dto.PointWinnerRequestDTO;
import org.example.tennisscoreboard.entity.MatchAndScore;
import org.example.tennisscoreboard.entity.Player;
import org.example.tennisscoreboard.entity.PointWinner;
import org.example.tennisscoreboard.entity.Score;
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
        Long playerOneId = matchAndScore.getMatch().getPlayerOne().getId();
        Score playerOneScore = matchAndScore.getGeneralScore().getPlayerOneScore();
        Score playerTwoScore = matchAndScore.getGeneralScore().getPlayerTwoScore();

        updatePoint(playerOneId, pointWinnerId, playerOneScore, playerTwoScore);
        updateGame(playerOneId, pointWinnerId, playerOneScore, playerTwoScore);
        updateSet(playerOneId, pointWinnerId, playerOneScore, playerTwoScore);
        updateWinner(playerOneId, pointWinnerId, playerOneScore, playerTwoScore, matchAndScore);

        return matchAndScoreMapper.toDto(matchAndScore);
    }

    private void updatePoint(Long playerOneId, Long pointWinnerId, Score playerOneScore, Score playerTwoScore) {
        int playerOneGames = playerOneScore.getGames();
        int playerTwoGames = playerTwoScore.getGames();

        if (hasTiebreak(playerOneGames, playerTwoGames)) {
            updatePointInTiebreak(pointWinnerId, playerOneId, playerOneScore, playerTwoScore);

        } else {
            updatePointInStandardGame(pointWinnerId, playerOneId, playerOneScore, playerTwoScore);
        }

        resetPoints(playerOneScore, playerTwoScore);
    }

    private boolean hasTiebreak(int playerOneGames, int playerTwoGames) {
        return playerOneGames == GAMES_IN_SET && playerTwoGames == GAMES_IN_SET;
    }

    private void updatePointInTiebreak(Long pointWinnerId, Long playerOneId, Score playerOneScore, Score playerTwoScore) {
        //7 win + разница в очка, если нет то играют дальше
        int playerOnePoints = playerOneScore.getPoints();
        int playerTwoPoints = playerTwoScore.getPoints();

        if (pointWinnerId.equals(playerOneId)) {
            playerOnePoints++;
            playerOneScore.setPoints(playerOnePoints);
            findWinnerInTiebreak(playerOneScore, playerOneScore, playerTwoScore);
        } else {
            playerTwoPoints++;
            playerTwoScore.setPoints(playerTwoPoints);
            findWinnerInTiebreak(playerTwoScore, playerOneScore, playerTwoScore);
        }
    }

    private void findWinnerInTiebreak(Score winnerScore, Score playerOneScore, Score playerTwoScore) {

        int highestPoints = Math.max(playerOneScore.getPoints(), playerTwoScore.getPoints());
        int lowestPoints = Math.min(playerOneScore.getPoints(), playerTwoScore.getPoints());

        boolean hasWinnerInTieBreak = (highestPoints >= POINTS_IN_TIEBREAK && (highestPoints - lowestPoints >= WIN_MARGIN));

        if (hasWinnerInTieBreak) {
            winnerScore.setPoints(RESET);
        }
    }

    //AD - кидать в DTO, а здесь реализовать по рамках числового формата
    private void updatePointInStandardGame(Long pointWinnerId, Long playerOneId, Score playerOneScore, Score playerTwoScore) {

        boolean hasTieScore = hasTieScore(playerOneScore, playerTwoScore);
        boolean hasAdvantage = hasAdvantage(playerOneScore, playerTwoScore);

        if (pointWinnerId.equals(playerOneId)) {
            updatePointInDifferentStage(hasTieScore, hasAdvantage, playerOneScore, playerTwoScore);
        } else {
            updatePointInDifferentStage(hasTieScore, hasAdvantage, playerTwoScore, playerOneScore);
        }
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

    private boolean hasTieScore(Score playerOneScore, Score playerTwoScore) {
        return playerOneScore.getPoints() == THREE_POINT && playerTwoScore.getPoints() == THREE_POINT;
    }

    private boolean hasAdvantage(Score playerOneScore, Score playerTwoScore) {
        return playerOneScore.getPoints() == AD || playerTwoScore.getPoints() == AD;
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

    private void resetPoints(Score playerOneScore, Score playerTwoScore) {
        int playerOnePoints = playerOneScore.getPoints();
        int playerTwoPoints = playerTwoScore.getPoints();

        if (hasResetPoints(playerOnePoints) || hasResetPoints(playerTwoPoints)) {
            playerOneScore.setPoints(ZERO_POINT);
            playerTwoScore.setPoints(ZERO_POINT);
        }
    }

    private boolean hasResetPoints(int points) {
        return points == RESET;
    }

    private void updateGame(Long playerOneId, Long pointWinnerId, Score playerOneScore, Score playerTwoScore) {
        //от pointWinner зависит кому дать game

        int playerOnePoints = playerOneScore.getPoints();
        int playerTwoPoints = playerTwoScore.getPoints();

        int playerOneGames = playerOneScore.getGames();
        int playerTwoGames = playerTwoScore.getGames();

        if (hasEndedGame(playerOnePoints, playerTwoPoints)) {
            if (pointWinnerId.equals(playerOneId)) {
                playerOneGames++;
                playerOneScore.setGames(playerOneGames);

            } else {
                playerTwoGames++;
                playerTwoScore.setGames(playerTwoGames);
            }
        }
        //присуждение reset
        resetGames(playerOneScore, playerTwoScore);
    }

    private boolean hasEndedGame(int playerOnePoints, int playerTwoPoints) {
        return playerOnePoints == ZERO_POINT && playerTwoPoints == ZERO_POINT;
    }

    private void resetGames(Score playerOneScore, Score playerTwoScore) {
        if (hasWinningSet(playerOneScore, playerTwoScore)) {
            playerOneScore.setGames(ZERO_POINT);
            playerTwoScore.setGames(ZERO_POINT);
        }
    }

    private boolean hasWinningSet(Score playerOneScore, Score playerTwoScore) {
        // max >=  GAMES_IN_SET  и max - min -> меньше на 2 очка

        int highestGames = Math.max(playerOneScore.getGames(), playerTwoScore.getGames());
        int lowestGames = Math.min(playerOneScore.getGames(), playerTwoScore.getGames());

        boolean hasDefaultWinningSet = highestGames >= GAMES_IN_SET && (highestGames - lowestGames >= WIN_MARGIN);
        boolean hasTiebreakWinningSet = highestGames > GAMES_IN_SET && lowestGames == GAMES_IN_SET;

        return hasDefaultWinningSet || hasTiebreakWinningSet;
    }

    private void updateSet(Long playerOneId, Long pointWinnerId, Score playerOneScore, Score playerTwoScore) {

        int playerOnePoints = playerOneScore.getPoints();
        int playerTwoPoints = playerTwoScore.getPoints();

        int playerOneGames = playerOneScore.getGames();
        int playerTwoGames = playerTwoScore.getGames();

        int playerOneSets = playerOneScore.getSets();
        int playerTwoSets = playerTwoScore.getSets();

        if (hasEndedSet(playerOnePoints, playerTwoPoints, playerOneGames, playerTwoGames)) {
            if (pointWinnerId.equals(playerOneId)) {
                playerOneSets++;
                playerOneScore.setSets(playerOneSets);
            } else {
                playerTwoSets++;
                playerTwoScore.setSets(playerTwoSets);
            }
        }
    }

    private boolean hasEndedSet(int playerOnePoints, int playerTwoPoints, int playerOneGames, int playerTwoGames) {
        return playerOnePoints == ZERO_POINT && playerTwoPoints == ZERO_POINT && playerOneGames == ZERO_POINT && playerTwoGames == ZERO_POINT;
    }

    private void updateWinner(Long playerOneId, Long pointWinnerId, Score playerOneScore, Score playerTwoScore, MatchAndScore matchAndScore) {
        int playerOneSets = playerOneScore.getSets();
        int playerTwoSets = playerTwoScore.getSets();

        if (playerOneSets == WIN_SETS_IN_ONGOING_MATCH || playerTwoSets == WIN_SETS_IN_ONGOING_MATCH) {
            if (pointWinnerId.equals(playerOneId)) {
                Player playerOne = matchAndScore.getMatch().getPlayerOne();
                matchAndScore.getMatch().setWinner(playerOne);
            } else {
                Player playerTwo = matchAndScore.getMatch().getPlayerTwo();
                matchAndScore.getMatch().setWinner(playerTwo);
            }
        }
    }
}
