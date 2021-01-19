package kr.co.ensecure.scheduler.base.spring;

import java.util.Properties;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.quartz.QuartzProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import kr.co.ensecure.scheduler.base.quartz.AutowiringSpringBeanJobFactory;
import kr.co.ensecure.scheduler.base.quartz.listener.JobsListener;
import kr.co.ensecure.scheduler.base.quartz.listener.TriggersListener;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
public class SpringQuartzConfig {

    @Autowired
    private DataSource dataSource;
    
    @Autowired
    private QuartzProperties quartzProperties;
    
    @Autowired
    private TriggersListener triggersListener;

    @Autowired
    private JobsListener jobsListener;
    
    @Bean
    @Primary
    public SchedulerFactoryBean schedulerFactoryBean(ApplicationContext applicationContext) {
        SchedulerFactoryBean schedulerFactoryBean = new SchedulerFactoryBean();
        
        AutowiringSpringBeanJobFactory jobFactory = new AutowiringSpringBeanJobFactory();
        jobFactory.setApplicationContext(applicationContext);
        schedulerFactoryBean.setJobFactory(jobFactory);
        
        Properties properties = new Properties();
        properties.putAll(quartzProperties.getProperties());
        
        schedulerFactoryBean.setGlobalTriggerListeners(triggersListener);
        schedulerFactoryBean.setGlobalJobListeners(jobsListener);
        schedulerFactoryBean.setOverwriteExistingJobs(true);
        schedulerFactoryBean.setDataSource(dataSource);     // Cluster 사용시 DataSource 셋팅 (in-memory 사용 시 삭제)
        schedulerFactoryBean.setQuartzProperties(properties);
        schedulerFactoryBean.setWaitForJobsToCompleteOnShutdown(true);
        
        return schedulerFactoryBean;
    }
}
