package org.example.tennisscoreboard.util;

import org.example.tennisscoreboard.dto.PlayersRequestDTO;

import java.security.InvalidParameterException;

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
}
