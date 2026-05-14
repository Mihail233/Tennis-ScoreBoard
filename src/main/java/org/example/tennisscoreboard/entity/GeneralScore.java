package org.example.tennisscoreboard.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GeneralScore {
    private Score playerOneScore;
    private Score playerTwoScore;
}
