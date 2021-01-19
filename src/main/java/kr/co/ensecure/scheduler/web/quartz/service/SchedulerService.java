package kr.co.ensecure.scheduler.web.quartz.service;

import java.util.Map;
import org.quartz.Job;
import org.quartz.JobKey;
import kr.co.ensecure.scheduler.web.quartz.vo.JobRequest;

public interface SchedulerService {
    /**
     * 등록된 모든 job 정보
     * @return
     */
    public Map<String, Object> getAllJobs();
    
    /**
     * 해당 job 실행 여부 확인
     * @param jobKey
     * @return
     */
    public boolean isJobRunning(JobKey jobKey);

    /**
     * 해당 job 등록 여부 확인
     * @param jobKey
     * @return
     */
    public boolean isJobExists(JobKey jobKey);

    /**
     * job 등록
     * @param jobRequest
     * @param jobClass
     * @return
     */
    public boolean addJob(JobRequest jobRequest, Class<? extends Job> jobClass);

    /**
     * job 삭제
     * @param jobKey
     * @return
     */
    public boolean deleteJob(JobKey jobKey);

    /**
     * job 비활성화
     * @param jobKey
     * @return
     */
    public boolean pauseJob(JobKey jobKey);

    /**
     * job 활성화
     * @param jobKey
     * @return
     */
    public boolean resumeJob(JobKey jobKey);

    /**
     * 해당 job의 스케줄링 상태
     * @param jobKey
     * @return
     */
    public String getJobState(JobKey jobKey);
    
    /**
     * 해당 job 즉시 실행
     * @param jobKey
     * @return
     */
    public boolean startJob(JobKey jobKey);

    /**
     * 해당 job 즉시 종료
     * @param jobKey
     * @return
     */
    public boolean stopJob(JobKey jobKey);

    /**
     * 해당 job 스케줄링 변경
     * @param jobRequest
     * @return
     */
    public boolean updateJob(JobRequest jobRequest);
}
