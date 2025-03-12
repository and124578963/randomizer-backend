package my.pervushin.randomizer.backend.repository;


import my.pervushin.randomizer.backend.entity.RandomNumber;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RandomNumberRepository extends JpaRepository<RandomNumber, Long> {
}