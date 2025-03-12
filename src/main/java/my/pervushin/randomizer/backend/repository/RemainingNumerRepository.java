package my.pervushin.randomizer.backend.repository;


import my.pervushin.randomizer.backend.entity.RemainingNumber;
import org.springframework.data.jpa.repository.JpaRepository;


public interface RemainingNumerRepository extends JpaRepository<RemainingNumber, Long> {
}