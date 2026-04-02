package my.pervushin.randomizer.backend.service;


import jakarta.transaction.Transactional;
import my.pervushin.randomizer.backend.entity.CurrentState;
import my.pervushin.randomizer.backend.entity.RandomNumber;
import my.pervushin.randomizer.backend.entity.RemainingNumber;
import my.pervushin.randomizer.backend.repository.CurrentStateRepository;
import my.pervushin.randomizer.backend.repository.RandomNumberRepository;
import my.pervushin.randomizer.backend.repository.RemainingNumerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Service
public class RandomizerService {

    private static final int MAX_WINNERS = 10;
    private static final int TOTAL_NUMBERS = 250;
    private static final int MAX_STAGES = 1;

    @Autowired
    private CurrentStateRepository currentStateRepository;

    @Autowired
    private RandomNumberRepository randomNumberRepository;

    @Autowired
    private RemainingNumerRepository remainingNumerRepository;

    @Autowired
    private org.springframework.jdbc.core.JdbcTemplate jdbcTemplate;

    @jakarta.annotation.PostConstruct
    public void init() {
        jdbcTemplate.execute("UPDATE current_state SET version = 0 WHERE version IS NULL");
    }

    private Random random = new Random();

    @Transactional
    public CurrentState getCurrentState() {
        return currentStateRepository.findById(1L).orElseGet(() -> {
            CurrentState state = new CurrentState();
            state.setCurrentStage(1);
            state.setCurrentValue(0);
            state.setRemainingClicks(MAX_WINNERS);
            state.setStatus("СТАРТ");
            state.setRemainingStages(MAX_STAGES);
            state.setDrawnNumbers(new ArrayList<>());
            state.addAllremainingNumbers(generateNumberList());
            return currentStateRepository.save(state);
        });
    }

    @Transactional
    public CurrentState reset() {
        randomNumberRepository.deleteAll();
        
        CurrentState state = currentStateRepository.findById(1L).orElseGet(() -> new CurrentState());
        
        // Очищаем старые невыпавшие числа. Благодаря orphanRemoval = true, 
        // JPA автоматически удалит их из базы.
        state.getRemainingNumbers().clear();
        
        state.setCurrentStage(1);
        state.setCurrentValue(0);
        state.setDrawnNumbers(new ArrayList<>());
        state.setRemainingClicks(MAX_WINNERS);
        state.setStatus("СТАРТ");
        state.setRemainingStages(MAX_STAGES);
        
        // Генерируем новые числа
        state.addAllremainingNumbers(generateNumberList());
        
        return currentStateRepository.save(state);
    }
    @Transactional
    public CurrentState generateNumber(boolean regenerate) {
        CurrentState state = getCurrentState();
        if (!regenerate &&state.getCurrentValue() != null && state.getCurrentValue() != 0) {
            throw new RuntimeException("Нужно сначала перевести состояние в следующий статус (очистить текущее значение).");
        }
        List<RemainingNumber> remaining = state.getRemainingNumbers();
        if (remaining.isEmpty()) {
            throw new RuntimeException("Нет невыпавших чисел.");
        }
        int index = random.nextInt(remaining.size());
        RemainingNumber newNumber = remaining.get(index);
        // Удаляем выбранное число из невыпавших
        remaining.remove(index);
        state.setRemainingNumbers(remaining);
        // Устанавливаем выбранное число как текущее
        state.setCurrentValue(newNumber.getNumber());
        // Регистрируем выпавшее число с информацией о этапе
        RandomNumber rn = new RandomNumber();
        rn.setValue(newNumber.getNumber());
        rn.setStage(state.getCurrentStage());
        randomNumberRepository.save(rn);
        if (regenerate) {
            if(state.getStatus().equals("НОМЕР ПЕРЕГЕНЕРИРОВАН")) {
                state.setStatus("НОМЕР ПЕРЕГЕНЕРИРОВАН2");
            } else {
                state.setStatus("НОМЕР ПЕРЕГЕНЕРИРОВАН");
            }
        } else {
            state.setStatus("НОМЕР СГЕНЕРИРОВАН");
        }
        currentStateRepository.save(state);
        return state;
    }

    /**
     * При вызове этого метода происходит перевод текущего состояния:
     * 1. Если сгенерировано число (currentValue != 0) – оно добавляется в список победителей
     *    и currentValue сбрасывается, remainingClicks уменьшается.
     * 2. Если currentValue уже 0 и нажатия закончились (remainingClicks == 0), происходит переход к новому этапу.
     */@Transactional
    public CurrentState advanceState() {
        CurrentState state = getCurrentState();
        System.out.println(state);
        if (state.getRemainingStages()  == 0 && state.getRemainingClicks() == MAX_WINNERS && state.getStatus().equals("НОВЫЙ ЭТАП")
                ) {
            System.out.println("КОНЕЦ");
            state.setStatus("КОНЕЦ");
            currentStateRepository.save(state);
            return state;

        }
        if (state.getStatus().equals("НОВЫЙ ЭТАП")) {
            state.setStatus("ГОТОВ");
            currentStateRepository.save(state);
            return state;

        }

        if (state.getCurrentValue() != null && state.getCurrentValue() != 0) {
            List<Integer> drawn = state.getDrawnNumbers();
            drawn.add(state.getCurrentValue());
            state.setDrawnNumbers(drawn);
            state.setCurrentValue(0);
            state.setRemainingClicks(state.getRemainingClicks() - 1);
            if (state.getRemainingClicks() <= 0) {
                state.setStatus("ЭТАП ЗАВЕРШЕН");
            } else {
                state.setStatus("ГОТОВ");
            }
            currentStateRepository.save(state);
            return state;
        } else {
            if (state.getRemainingClicks() <= 0) {
                // Переход к следующему этапу
                state.setCurrentStage(state.getCurrentStage() + 1);
                state.setRemainingStages(state.getRemainingStages() - 1);
                state.setDrawnNumbers(new ArrayList<>());
                state.setRemainingClicks(MAX_WINNERS);
                state.setCurrentValue(0);
                state.setStatus("НОВЫЙ ЭТАП");
                currentStateRepository.save(state);
                return state;
            } else {
                throw new RuntimeException("Нет текущего сгенерированного числа для перевода.");
            }
        }
    }

    private CurrentState initializeState() {
        CurrentState state = new CurrentState();
        state.setCurrentStage(1);
        state.setCurrentValue(0);
        state.addAllremainingNumbers(generateNumberList());
        state.setDrawnNumbers(new ArrayList<>());
        state.setRemainingClicks(MAX_WINNERS);
        state.setStatus("СТАРТ");
        state.setRemainingStages(MAX_STAGES);
        return state;
    }

    private List<RemainingNumber> generateNumberList() {
        List<RemainingNumber> numbers = new ArrayList<>();
        for (int i = 1; i <= TOTAL_NUMBERS; i++) {
            RemainingNumber rn = new RemainingNumber();
            rn.setNumber(i);
            numbers.add(rn);
        }
        return numbers;
    }
}