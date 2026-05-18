package org.example.tennisscoreboard.service.match;

import org.example.tennisscoreboard.dto.MatchAndScoreResponseDTO;
import org.example.tennisscoreboard.dto.PointWinnerRequestDTO;
import org.example.tennisscoreboard.entity.*;
import org.example.tennisscoreboard.mapper.MatchAndScoreMapper;
import org.example.tennisscoreboard.mapper.PointWinnerMapper;

public class MatchScoreCalculationService {
    private static final int MINIMUM_POINTS_IN_TIEBREAK = 7;
    private static final int MINIMUM_GAMES_IN_SET = 6;
    private static final int MINIMUM_MARGIN = 2;
    private static final int SETS_TO_WIN_MATCH = 2;
    private static final int AD = -1;
    private static final int RESET = -2;

    private static final int LOVE = 0;
    private static final int ONE_POINT = 15;
    private static final int TWO_POINT = 30;
    private static final int THREE_POINT = 40;

    private final PointWinnerMapper pointWinnerMapper = PointWinnerMapper.INSTANCE;
    private final MatchAndScoreMapper matchAndScoreMapper = MatchAndScoreMapper.INSTANCE;

    public MatchAndScoreResponseDTO updateMatchScore(PointWinnerRequestDTO pointWinnerRequestDTO, MatchAndScoreResponseDTO matchAndScoreResponseDTO) {

        PointWinner pointWinner = pointWinnerMapper.toObject(pointWinnerRequestDTO);
        MatchAndScore matchAndScore = matchAndScoreMapper.toObject(matchAndScoreResponseDTO);
        Match match = matchAndScore.getMatch();

        Long playerOneId = match.getPlayerOne().getId();
        Long pointWinnerId = Long.valueOf(pointWinner.getPointWinnerId());

        Score playerOneScore = matchAndScore.getGeneralScore().getPlayerOneScore();
        Score playerTwoScore = matchAndScore.getGeneralScore().getPlayerTwoScore();

        if (playerOneId.equals(pointWinnerId)) {
            updateMatchScoreCertainPlayer(playerOneScore, playerTwoScore, match.getPlayerOne(), match);
        } else {
            updateMatchScoreCertainPlayer(playerTwoScore, playerOneScore, match.getPlayerTwo(), match);
        }

        return matchAndScoreMapper.toDto(matchAndScore);
    }

    private void updateMatchScoreCertainPlayer(Score winnerScore, Score loserScore, Player player, Match match) {
        updatePoint(winnerScore, loserScore);
        updateGame(winnerScore, loserScore);
        updateSet(winnerScore, loserScore);
        updateWinner(winnerScore, player, match);
    }

    private void updatePoint(Score winnerScore, Score loserScore) {

        if (hasTiebreak(winnerScore.getGames(), loserScore.getGames())) {
            updatePointInTiebreak(winnerScore, loserScore);

        } else {
            updatePointInStandardGame(winnerScore, loserScore);
        }

        resetPoints(winnerScore, loserScore);
    }

    private boolean hasTiebreak(int winnerGames, int loserGames) {
        return winnerGames == MINIMUM_GAMES_IN_SET && loserGames == MINIMUM_GAMES_IN_SET;
    }

    private void updatePointInTiebreak(Score winnerScore, Score loserScore) {
        //7 win + нужна разница в 2 очка, если нет то играют дальше
        int winnerPoints = winnerScore.getPoints();
        winnerPoints++;
        winnerScore.setPoints(winnerPoints);

        findWinnerInTiebreak(winnerScore, loserScore);
    }

    private void findWinnerInTiebreak(Score winnerScore, Score loserScore) {

        int highestPoints = Math.max(winnerScore.getPoints(), loserScore.getPoints());
        int lowestPoints = Math.min(winnerScore.getPoints(), loserScore.getPoints());

        boolean hasWinnerInTieBreak = (highestPoints >= MINIMUM_POINTS_IN_TIEBREAK && (highestPoints - lowestPoints >= MINIMUM_MARGIN));

        if (hasWinnerInTieBreak) {
            winnerScore.setPoints(RESET);
        }
    }

    //AD - кидать в DTO, а здесь реализовать по рамках числового формата
    private void updatePointInStandardGame(Score winnerScore, Score loserScore) {

        boolean hasDeuce = hasDeuce(winnerScore, loserScore);
        boolean hasAdvantage = hasAdvantage(winnerScore, loserScore);

        updatePointInDifferentStage(hasDeuce, hasAdvantage, winnerScore, loserScore);
    }

    private void updatePointInDifferentStage(boolean hasDeuce, boolean hasAdvantage, Score winnerScore, Score loserScore) {
        if (hasDeuce) {
            winnerScore.setPoints(AD);

            //Advantage - AD
        } else if (hasAdvantage) {

            if (winnerScore.getPoints() == AD) {
                winnerScore.setPoints(RESET);
            } else {
                loserScore.setPoints(THREE_POINT);
            }

        } else {
            awardPoint(winnerScore);
        }
    }

    private boolean hasDeuce(Score winnerScore, Score loserScore) {
        return winnerScore.getPoints() == THREE_POINT && loserScore.getPoints() == THREE_POINT;
    }

    private boolean hasAdvantage(Score winnerScore, Score loserScore) {
        return winnerScore.getPoints() == AD || loserScore.getPoints() == AD;
    }

    private void awardPoint(Score score) {

        switch (score.getPoints()) {
            case (LOVE):
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

        if (hasResetPoints(winnerScore.getPoints()) || hasResetPoints(loserScore.getPoints())) {
            winnerScore.setPoints(LOVE);
            loserScore.setPoints(LOVE);
        }
    }

    private boolean hasResetPoints(int points) {
        return points == RESET;
    }

    private void updateGame(Score winnerScore, Score loserScore) {
        //от pointWinner зависит кому дать GAME
        int winnerGames = winnerScore.getGames();

        if (hasEndedGame(winnerScore.getPoints(), loserScore.getPoints())) {
            winnerGames++;
            winnerScore.setGames(winnerGames);
        }

        resetGames(winnerScore, loserScore);
    }

    private boolean hasEndedGame(int winnerPoints, int loserPoints) {
        return winnerPoints == LOVE && loserPoints == LOVE;
    }

    private void resetGames(Score winnerScore, Score loserScore) {
        if (hasWinningSet(winnerScore, loserScore)) {
            winnerScore.setGames(LOVE);
            loserScore.setGames(LOVE);
        }
    }

    private boolean hasWinningSet(Score winnerScore, Score loserScore) {
        // (max >= MINIMUM_GAMES_IN_SET) и (max - min -> разница в 2 очка)

        int highestGames = Math.max(winnerScore.getGames(), loserScore.getGames());
        int lowestGames = Math.min(winnerScore.getGames(), loserScore.getGames());

        boolean hasDefaultWinningSet = highestGames >= MINIMUM_GAMES_IN_SET && (highestGames - lowestGames >= MINIMUM_MARGIN);
        boolean hasTiebreakWinningSet = highestGames > MINIMUM_GAMES_IN_SET && lowestGames == MINIMUM_GAMES_IN_SET;

        return hasDefaultWinningSet || hasTiebreakWinningSet;
    }

    private void updateSet(Score winnerScore, Score loserScore) {
        int winnerSets = winnerScore.getSets();

        if (hasEndedSet(winnerScore.getPoints(), loserScore.getPoints(), winnerScore.getGames(), loserScore.getGames())) {
            winnerSets++;
            winnerScore.setSets(winnerSets);
        }
    }

    private boolean hasEndedSet(int winnerPoints, int loserPoints, int winnerGames, int loserGames) {
        return winnerPoints == LOVE && loserPoints == LOVE && winnerGames == LOVE && loserGames == LOVE;
    }

    private void updateWinner(Score winnerScore, Player winner, Match match) {
        if (winnerScore.getSets() == SETS_TO_WIN_MATCH) {
            match.setWinner(winner);
        }
    }
}
