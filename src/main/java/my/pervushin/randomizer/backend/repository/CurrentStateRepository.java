package my.pervushin.randomizer.backend.repository;

import my.pervushin.randomizer.backend.entity.CurrentState;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CurrentStateRepository extends JpaRepository<CurrentState, Long> {
}