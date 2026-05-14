package org.example.tennisscoreboard.entity;


import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
@Entity
@Table(name = "Players", indexes = {
        @Index(columnList = "name", unique = true, name = "player_name_ind")
})
public class Player {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NonNull
    @Column(unique = true)
    private String name;

    //@OneToMany - один игрок множество матчей, при этом возратятся все матчи игрока
}
