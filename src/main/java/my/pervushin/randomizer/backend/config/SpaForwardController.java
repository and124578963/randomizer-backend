package my.pervushin.randomizer.backend.config;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class SpaForwardController {

    @GetMapping("/visualization")
    public String visualization() {
        return "forward:/index.html";
    }
}
