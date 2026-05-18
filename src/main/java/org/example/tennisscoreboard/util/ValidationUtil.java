package org.example.tennisscoreboard.util;

import org.example.tennisscoreboard.dto.MatchAndScoreResponseDTO;
import org.example.tennisscoreboard.dto.PlayersRequestDTO;
import org.example.tennisscoreboard.dto.PointWinnerRequestDTO;
import org.example.tennisscoreboard.entity.Match;
import org.example.tennisscoreboard.exception.TestException;

import java.security.InvalidParameterException;
import java.util.regex.Pattern;

public class ValidationUtil {

    public static void validate(PlayersRequestDTO playersRequestDTO) {
        String playerOneName = playersRequestDTO.playerOneName();
        String playerTwoName = playersRequestDTO.playerTwoName();

        if (playerOneName == null || playerOneName.isBlank()) {
            throw new InvalidParameterException("Missing parameter - playerOneName");
        }

        if (playerTwoName == null || playerTwoName.isBlank()) {
            throw new InvalidParameterException("Missing parameter - playerTwoName");
        }
    }

    public static void validate(PointWinnerRequestDTO pointWinnerRequestDTO, MatchAndScoreResponseDTO matchAndScoreResponseDTO) {
        String pointWinnerId = pointWinnerRequestDTO.pointWinnerId();
        Long playerOneId = matchAndScoreResponseDTO.match().playerOne().id();
        Long playerTwoId = matchAndScoreResponseDTO.match().playerTwo().id();

        if (pointWinnerId == null || pointWinnerId.isBlank()) {
            throw new InvalidParameterException("Missing parameter - pointWinner");
        }

        Long pointWinnerLongId = parseString(pointWinnerId);
        if (!playerOneId.equals(pointWinnerLongId) && !playerTwoId.equals(pointWinnerLongId)) {
            //можно подумать над текстом ошибки
            throw new InvalidParameterException("Invalid point winner id");
        }
    }

    private static Long parseString(String pointWinnerId) {
        try {
            return Long.valueOf(pointWinnerId);
        } catch (NumberFormatException e) {
            //можно подумать над текстом ошибки
            throw new InvalidParameterException("Invalid point winner id");
        }
    }

    public static void validateUuid(String uuid) {
        if (uuid == null || uuid.isBlank()) {
            throw new InvalidParameterException("Missing parameter - UUID");
        }

        Pattern UUID_REGEX =
                Pattern.compile("^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$");

        if (!UUID_REGEX.matcher(uuid).matches()) {
            throw new InvalidParameterException("Invalid uuid");
        }
    }
}
