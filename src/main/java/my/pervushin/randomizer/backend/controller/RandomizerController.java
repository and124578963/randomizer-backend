package my.pervushin.randomizer.backend.controller;

import my.pervushin.randomizer.backend.entity.CurrentState;
import my.pervushin.randomizer.backend.service.RandomizerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class RandomizerController {

    @Autowired
    private RandomizerService randomizerService;

    // API для получения текущего состояния
    @GetMapping("/state")
    public CurrentState getState() {
        return randomizerService.getCurrentState();
    }

    // API для генерации нового числа в текущем этапе
    @PostMapping("/generate")
    public CurrentState generateNumber() {
        return randomizerService.generateNumber();
    }

    // API для перевода текущего состояния в следующий статус
    // Если сгенерировано число – переводит его в список “выпавших”, иначе – переходит к следующему этапу (если нажатия закончились)
    @PostMapping("/advance")
    public CurrentState advanceState() {
        return randomizerService.advanceState();
    }

    // API для обнуления всех данных
    @PostMapping("/reset")
    public CurrentState reset() {
        return randomizerService.reset();
    }
}