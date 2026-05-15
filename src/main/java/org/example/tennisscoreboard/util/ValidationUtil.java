package org.example.tennisscoreboard.util;

import org.example.tennisscoreboard.dto.PlayersRequestDTO;
import org.example.tennisscoreboard.dto.PointWinnerRequestDTO;
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

    public static void validate(PointWinnerRequestDTO pointWinnerRequestDTO) {
        String pointWinner = pointWinnerRequestDTO.pointWinnerId();

        if (pointWinner == null || pointWinner.isBlank()) {
            throw new InvalidParameterException("Missing parameter - pointWinner");
        }
    }

    public static void validateUuid(String uuid) {
        Pattern UUID_REGEX =
                Pattern.compile("^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$");

        if (uuid == null || uuid.isBlank()) {
            throw new InvalidParameterException("Missing parameter - UUID");
        }

        if (!UUID_REGEX.matcher(uuid).matches()) {
            throw new InvalidParameterException("Invalid uuid");
        }
    }
}
