package my.pervushin.randomizer.backend.entity;

import jakarta.persistence.*;
import lombok.Data;


@Data
@Entity
@Table(name = "random_numbers")
public class RandomNumber {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer value;

    private Integer stage;
}
