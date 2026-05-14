package org.example.tennisscoreboard.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Score {
    private int points = 0;
    private int games = 0;
    private int sets = 0;
}
