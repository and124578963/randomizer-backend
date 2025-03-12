package my.pervushin.randomizer.backend.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonValue;
import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;

@Data
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Entity
public class RemainingNumber {
    @Id
    @SequenceGenerator(
            name = "remainingNumberGenerator",
            sequenceName = "remaining_number_seq",
            allocationSize = 30
    )
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "remainingNumberGenerator")
    private Long id;

    private Integer number;


    @ToString.Exclude
    // связь с CurrentState
    @JsonBackReference
    @ManyToOne
    private CurrentState state;

    @JsonValue
    public Integer asJson() {
        return number;
    }
    // геттеры/сеттеры
}