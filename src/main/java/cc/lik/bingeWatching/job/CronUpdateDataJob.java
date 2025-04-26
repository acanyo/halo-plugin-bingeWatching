package cc.lik.bingeWatching.job;

import cc.lik.bingeWatching.entity.HandsomeMovie;
import cc.lik.bingeWatching.vo.HandsomeMovieVo;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.Duration;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import run.halo.app.extension.ExtensionClient;
import run.halo.app.extension.ReactiveExtensionClient;
import reactor.core.publisher.Mono;

@Component
@EnableScheduling
@AllArgsConstructor
@Slf4j
public class CronUpdateDataJob {
    private ReactiveExtensionClient client;

    @Scheduled(cron = "0 0 1 * * ?")
    public void update() {
        int currentDayOfWeek = LocalDateTime.now().getDayOfWeek().getValue();
        log.info("当前是周{}", currentDayOfWeek);
        
        client.list(HandsomeMovie.class, movie -> {
            String status = movie.getSpec().getStatus();
            String updateCycle = movie.getSpec().getUpdateCycle();
            if (!"观看中".equals(status) || updateCycle == null || updateCycle.isBlank()) {
                return false;
            }
            return java.util.Arrays.stream(updateCycle.split(","))
                    .map(String::trim)
                    .anyMatch(day -> day.equals(String.valueOf(currentDayOfWeek)));
        }, null)
        .flatMap(movie -> {
            try {
                String currentSeen = movie.getSpec().getSeen();
                int seen = Integer.parseInt(currentSeen);
                movie.getSpec().setSeen(String.valueOf(seen + 1));
                return client.update(movie);
            } catch (NumberFormatException e) {
                log.error("更新电影集数失败，当前集数格式错误: {}", movie.getSpec().getSeen(), e);
                return Mono.empty();
            }
        })
        .timeout(Duration.ofSeconds(30))
        .onErrorResume(e -> {
            log.error("更新电影集数失败", e);
            return Mono.empty();
        })
        .subscribe();
    }
}
