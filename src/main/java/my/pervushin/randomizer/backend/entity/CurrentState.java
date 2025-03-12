package my.pervushin.randomizer.backend.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Data;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.BatchSize;

import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "current_state")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class CurrentState {

    // Фиксированный идентификатор для синглтона
    @Id
    private Long id = 1L;

    // Текущее значение на экране
    private Integer currentValue;

    // Текущий этап
    private Integer currentStage;


    // Невыпавшие числа
    @OneToMany(mappedBy = "state", cascade = CascadeType.ALL, orphanRemoval = true)
    @BatchSize(size = 30)
    @JsonManagedReference
    private List<RemainingNumber> remainingNumbers = new ArrayList<>();

    // Выпавшие числа в текущем этапе
    @ElementCollection
    @CollectionTable(name = "drawn_numbers", joinColumns = @JoinColumn(name = "state_id"))
    @Column(name = "number")
    private List<Integer> drawnNumbers = new ArrayList<>();

    // Осталось нажатий (например – число розыгрышей в этапе)
    private Integer remainingClicks;

    private Integer remainingStages;

    // Статус (например: ГОТОВ, НОМЕР СГЕНЕРИРОВАН, ЭТАП ЗАВЕРШЕН и т.д.)
    @Setter
    private String status;

    public void addAllremainingNumbers(List<RemainingNumber> remainingNumbers) {
        this.remainingNumbers.addAll(remainingNumbers);
        remainingNumbers.forEach(remainingNumber -> remainingNumber.setState(this));
    }

}