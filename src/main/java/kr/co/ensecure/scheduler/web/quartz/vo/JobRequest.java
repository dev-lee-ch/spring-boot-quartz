package kr.co.ensecure.scheduler.web.quartz.vo;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import org.springframework.format.annotation.DateTimeFormat;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class JobRequest {
    private String jobGroup = "DEFAULT";
    private String jobName;
    
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startDateAt;
    private long repeatIntervalInSeconds;
    private int repeatCount;

    private String cronExpression;
    private Map<String, Object> jobDataMap = new HashMap<String, Object>();
    
    public boolean isJobTypeSimple() {
        return this.cronExpression == null;
    }

    public String getCurrentJobType() {
        return isJobTypeSimple() ? "SIMPLE" : "CRON";
    }
}
