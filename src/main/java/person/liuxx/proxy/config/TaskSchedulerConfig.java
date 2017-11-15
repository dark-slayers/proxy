package person.liuxx.proxy.config;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

/**
 * @author 刘湘湘
 * @version 1.0.0<br>
 *          创建时间：2017年8月30日 下午4:35:16
 * @since 1.0.0
 */
@Configuration
@ComponentScan("person.liuxx.movie.task")
@EnableScheduling
public class TaskSchedulerConfig implements SchedulingConfigurer
{
    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar)
    {
        taskRegistrar.setScheduler(taskExecutor());
    }

    @Bean(destroyMethod = "shutdown")
    public Executor taskExecutor()
    {
        return Executors.newScheduledThreadPool(10);
    }
}
