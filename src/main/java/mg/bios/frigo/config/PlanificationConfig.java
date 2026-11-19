package mg.bios.frigo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

@Configuration
@EnableScheduling
public class PlanificationConfig implements SchedulingConfigurer {

    @Bean(name = "planificateur")
    public ThreadPoolTaskScheduler planificateur() {
        ThreadPoolTaskScheduler planificateur = new ThreadPoolTaskScheduler();
        planificateur.setPoolSize(2);
        planificateur.setThreadNamePrefix("frigo-planif-");
        return planificateur;
    }

    // Les méthodes @Scheduled utilisent ce minuteur
    @Override
    public void configureTasks(ScheduledTaskRegistrar registrar) {
        registrar.setScheduler(planificateur());
    }
}