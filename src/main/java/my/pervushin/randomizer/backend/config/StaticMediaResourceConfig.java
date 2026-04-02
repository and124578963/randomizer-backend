package my.pervushin.randomizer.backend.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * CRA кладёт импортированные ассеты в {@code build/static/media/} (URL {@code /static/media/...}).
 * Файлы из {@code public/media/} попадают в {@code build/media/} (ожидаемый URL был бы {@code /media/...}).
 * Явная раздача {@code /static/media/**} с двумя корнями закрывает оба варианта.
 */
@Configuration
public class StaticMediaResourceConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/static/media/**")
                .addResourceLocations(
                        "file:/app/static/static/media/",
                        "file:/app/static/media/");
    }
}
