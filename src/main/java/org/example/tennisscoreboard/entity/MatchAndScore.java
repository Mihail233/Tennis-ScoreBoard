package org.example.tennisscoreboard.entity;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MatchAndScore {
    private Match match;
    private GeneralScore generalScore;
}
