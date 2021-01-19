package kr.co.ensecure.scheduler.web.quartz.service.impl;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.quartz.Job;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerKey;
import org.quartz.impl.matchers.GroupMatcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.stereotype.Component;
import kr.co.ensecure.scheduler.base.quartz.creator.JobCreator;
import kr.co.ensecure.scheduler.base.util.DateFormatUtil;
import kr.co.ensecure.scheduler.web.quartz.service.SchedulerService;
import kr.co.ensecure.scheduler.web.quartz.vo.JobRequest;
import kr.co.ensecure.scheduler.web.quartz.vo.JobResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class SchedulerServiceImpl implements SchedulerService {
    
    @Autowired
    private ApplicationContext context;
    
    @Autowired
    private SchedulerFactoryBean schedulerFactoryBean;
    
    /**
     *  등록 된 모든 job 정보 조회
     *  numOfAllJobs: 등록 된 job 수
     *  numOfGroups: 등록 된 group 수  
     *  jobs: 등록된 job 정보
     *      job.jobName: job 명
     *      job.groupName: 그룹 명
     *      job.jobStatus: job 상태
     *      job.scheduleTime: job 등록 시간
     *      job.lastFiredTime: job 마지막 실행 시간
     *      job.nextFireTime: job 다음 실행 시간
     *  numOfRunningJobs: 실행 중인 job 수
     *  runndingJobs: 실행 중인 job 정보
     *      job.jobName: job 명
     *      job.groupName: 그룹 명
     */
    @SuppressWarnings("unchecked")
    @Override
    public Map<String, Object> getAllJobs() {
        JobResponse jobResponse;
        Map<String, Object> jobStatusResponse = new HashMap<String, Object>();
        List<JobResponse> jobs = new ArrayList<JobResponse>();          // 등록된 모든 job 정보
        List<JobResponse> runningJobs = new ArrayList<JobResponse>();   // 실행중인 모든 job 정보
        int numOfRunningJobs = 0;   // 실행중인 job 수
        int numOfGroups = 0;        // 등록된 group 수
        int numOfAllJobs = 0;       // 등록된 job 수

        try {
            Scheduler scheduler = schedulerFactoryBean.getScheduler();
            for (String groupName : scheduler.getJobGroupNames()) {
                numOfGroups++;
                for (JobKey jobKey : scheduler.getJobKeys(GroupMatcher.jobGroupEquals(groupName))) {
                    List<Trigger> triggers = (List<Trigger>) scheduler.getTriggersOfJob(jobKey);
                    jobResponse = JobResponse.builder()
                            .jobName(jobKey.getName())
                            .groupName(jobKey.getGroup())
                            .scheduleTime(DateFormatUtil.formatDateTime(triggers.get(0).getStartTime()))
                            .lastFiredTime(DateFormatUtil.formatDateTime(triggers.get(0).getPreviousFireTime()))
                            .nextFireTime(DateFormatUtil.formatDateTime(triggers.get(0).getNextFireTime()))
                            .build();

                    if (isJobRunning(jobKey)) {
                        jobResponse.setJobStatus("RUNNING");
                        runningJobs.add(jobResponse);
                        numOfRunningJobs++;
                    } else {
                        String jobState = getJobState(jobKey);
                        jobResponse.setJobStatus(jobState);
                    }
                    numOfAllJobs++;
                    jobs.add(jobResponse);
                }
            }
        } catch (SchedulerException e) {
            log.error("[schedulerdebug] error while fetching all job info", e);
        }

        jobStatusResponse.put("numOfAllJobs", numOfAllJobs);
        jobStatusResponse.put("numOfRunningJobs", numOfRunningJobs);
        jobStatusResponse.put("numOfGroups", numOfGroups);
        jobStatusResponse.put("jobs", jobs);
        jobStatusResponse.put("runningJobs", runningJobs);
        
        return jobStatusResponse;
    }
    
    /**
     * 해당 job 실행 여부 확인
     */
    @Override
    public boolean isJobRunning(JobKey jobKey) {
        try {
            List<JobExecutionContext> currentJobs = schedulerFactoryBean.getScheduler().getCurrentlyExecutingJobs();
            if (currentJobs != null) {
                for (JobExecutionContext jobCtx : currentJobs) {
                    if (jobKey.getName().equals(jobCtx.getJobDetail().getKey().getName())) {
                        return true;
                    }
                }
            }
        } catch (SchedulerException e) {
            log.error("[schedulerdebug] error occurred while checking job with jobKey : {}", jobKey, e);
        }
        return false;
    }
    
    /**
     * 해당 job 등록 여부 확인
     */
    @Override
    public boolean isJobExists(JobKey jobKey) {
        try {
            Scheduler scheduler = schedulerFactoryBean.getScheduler();
            if (scheduler.checkExists(jobKey)) {
                return true;
            }
        } catch (SchedulerException e) {
            log.error("[schedulerdebug] error occurred while checking job exists :: jobKey : {}", jobKey, e);
        }
        return false;
    }
    
    /**
     * job 등록
     */
    @Override
    public boolean addJob(JobRequest jobRequest, Class<? extends Job> jobClass) {
        JobKey jobKey = null;
        JobDetail jobDetail;
        Trigger trigger;

        try {
            trigger = JobCreator.createTrigger(jobRequest);
            jobDetail = JobCreator.createJob(jobRequest, jobClass, context);
            jobKey = JobKey.jobKey(jobRequest.getJobName(), jobRequest.getJobGroup());

            Date dt = schedulerFactoryBean.getScheduler().scheduleJob(jobDetail, trigger);
            log.debug("Job with jobKey : {} scheduled successfully at date : {}", jobDetail.getKey(), dt);
            return true;
        } catch (SchedulerException e) {
            log.error("error occurred while scheduling with jobKey : {}", jobKey, e);
        }
        return false;
    }

    /**
     * job 삭제
     */
    @Override
    public boolean deleteJob(JobKey jobKey) {
        log.debug("[schedulerdebug] deleting job with jobKey : {}", jobKey);
        try {
            return schedulerFactoryBean.getScheduler().deleteJob(jobKey);
        } catch (SchedulerException e) {
            log.error("[schedulerdebug] error occurred while deleting job with jobKey : {}", jobKey, e);
        }
        return false;
    }

    /**
     * job 비활성화
     */
    @Override
    public boolean pauseJob(JobKey jobKey) {
        //todo : job history에도 기록하도록 함.
        log.debug("[schedulerdebug] pausing job with jobKey : {}", jobKey);
        try {
            schedulerFactoryBean.getScheduler().pauseJob(jobKey);
            return true;
        } catch (SchedulerException e) {
            log.error("[schedulerdebug] error occurred while deleting job with jobKey : {}", jobKey, e);
        }
        return false;
    }

    /**
     * job 활성화
     */
    @Override
    public boolean resumeJob(JobKey jobKey) {
        //todo : job history에도 기록하도록 함.
        log.debug("[schedulerdebug] resuming job with jobKey : {}", jobKey);
        try {
            schedulerFactoryBean.getScheduler().resumeJob(jobKey);
            return true;
        } catch (SchedulerException e) {
            log.error("[schedulerdebug] error occurred while resuming job with jobKey : {}", jobKey, e);
        }
        return false;
    }
    
    /**
     * job의 스케줄링 상태 조회
     */
    @Override
    public String getJobState(JobKey jobKey) {
        try {
            Scheduler scheduler = schedulerFactoryBean.getScheduler();
            JobDetail jobDetail = scheduler.getJobDetail(jobKey);

            List<? extends Trigger> triggers = scheduler.getTriggersOfJob(jobDetail.getKey());

            if (triggers != null && triggers.size() > 0) {
                for (Trigger trigger : triggers) {
                    Trigger.TriggerState triggerState = scheduler.getTriggerState(trigger.getKey());
                    if (Trigger.TriggerState.NORMAL.equals(triggerState)) {
                        return "SCHEDULED";
                    }
                    return triggerState.name().toUpperCase();
                }
            }
        } catch (SchedulerException e) {
            log.error("[schedulerdebug] Error occurred while getting job state with jobKey : {}", jobKey, e);
        }
        return null;
    }

    /**
     * job 즉시 실행
     */
    @Override
    public boolean startJob(JobKey jobKey) {
        try {
            schedulerFactoryBean.getScheduler().triggerJob(jobKey);
            log.debug("Job with jobKey : {} execute successfully ", jobKey);
            return true;
        } catch (SchedulerException e) {
            log.error("error occurred while execute with jobKey : {}", jobKey, e);
        }
        return false;
    }
    
    /**
     * job 즉시 종료
     */
    @Override
    public boolean stopJob(JobKey jobKey) {
        try {
            schedulerFactoryBean.getScheduler().interrupt(jobKey);
            log.debug("Job with jobKey : {} stop successfully ", jobKey);
            return true;
        } catch (SchedulerException e) {
            log.error("error occurred while stop with jobKey : {}", jobKey, e);
        }
        return false;
        
    }

    /**
     * job 스케줄링 수정
     */
    @Override
    public boolean updateJob(JobRequest jobRequest) {
        JobKey jobKey = null;
        Trigger newTrigger;

        try {
            newTrigger = JobCreator.createTrigger(jobRequest);
            jobKey = JobKey.jobKey(jobRequest.getJobName(), jobRequest.getJobGroup());

            Date dt = schedulerFactoryBean.getScheduler().rescheduleJob(TriggerKey.triggerKey(jobRequest.getJobName()), newTrigger);
            log.debug("Job with jobKey : {} rescheduled successfully at date : {}", jobKey, dt);
            return true;
        } catch (SchedulerException e) {
            log.error("error occurred while scheduling with jobKey : {}", jobKey, e);
        }
        return false;
    }
}
