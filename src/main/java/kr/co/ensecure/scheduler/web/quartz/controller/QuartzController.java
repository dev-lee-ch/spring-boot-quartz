package kr.co.ensecure.scheduler.web.quartz.controller;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.quartz.CronExpression;
import org.quartz.JobKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import kr.co.ensecure.scheduler.base.api.ApiResponse;
import kr.co.ensecure.scheduler.job.ProcessorExecutorJob;
import kr.co.ensecure.scheduler.web.quartz.service.SchedulerService;
import kr.co.ensecure.scheduler.web.quartz.vo.JobRequest;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("api/schd")
public class QuartzController {
    
    @Autowired
    private SchedulerService schedulerService;
    
    
    /**
     * 등록된 Job 전체 조회
     * @return
     */
    @GetMapping(value = "/status")
    public ResponseEntity<?> getAllJobs() {
        Map<String, Object> resultMap = new HashMap<String, Object>();
        resultMap.put("result", schedulerService.getAllJobs());
        return new ResponseEntity<>(ApiResponse.createApiResponse(resultMap), HttpStatus.OK);
    }
    
    /**
     * Job 등록 요청
     * @param jobRequest
     * @return
     */
    @PostMapping(value = "/addJob")
    public ResponseEntity<?> addScheduleJob(@RequestBody JobRequest  jobRequest) {
        log.info("add schedule job :: jobRequest : {}", jobRequest);
        
        boolean result = false;
        String msg = null;
        String code = "1000";   // [?]은 필수 값 입니다.
        List<Object> codeParam = new ArrayList<Object>(); 
        
        if (jobRequest.getJobName() == null) {
            codeParam.add("jobName");
        } else if (jobRequest.getCronExpression() == null) {
            codeParam.add("cronExpression");
        } else if (!CronExpression.isValidExpression(jobRequest.getCronExpression())) {
            code = "1002";  // 잘못된 표현식 입니다. [?]
            codeParam.add(jobRequest.getCronExpression());
        } else if (jobRequest.getJobDataMap() == null) {            
            codeParam.add("jobDataMap");
        } else if (!jobRequest.getJobDataMap().containsKey("path") || !jobRequest.getJobDataMap().containsKey("file")) {
            codeParam.add("jobDataMap[path] / jobDataMap[file]");
            
        } else {
            if (new File((String)jobRequest.getJobDataMap().get("path"), (String)jobRequest.getJobDataMap().get("file")).exists()) {
                JobKey jobKey = new JobKey(jobRequest.getJobName(), jobRequest.getJobGroup());
                if (!schedulerService.isJobExists(jobKey)) {
                    if (jobRequest.isJobTypeSimple()) {
                        schedulerService.addJob(jobRequest, ProcessorExecutorJob.class);
                    } else {
                        schedulerService.addJob(jobRequest, ProcessorExecutorJob.class);
                    }
                    result = true;
                    code = "200";   // 정상
                } else {
                    code = "1001";  // 이미 등록된 작업명 입니다. [?]
                    codeParam.add(jobRequest.getJobName());                    
                }
            } else {
                code = "1003";  // 파일이 존재하지 않습니다. [?]
                codeParam.add(jobRequest.getJobDataMap().get("path") + "/" + (String)jobRequest.getJobDataMap().get("file"));
            }
        }
        
        Map<String, Object> resultMap = new HashMap<String, Object>();
        resultMap.put("code", code);
        resultMap.put("codeParam", codeParam.toArray());
        resultMap.put("result", result);
        resultMap.put("msg", msg);
        
        return new ResponseEntity<>(ApiResponse.createApiResponse(resultMap), result ? HttpStatus.OK : HttpStatus.BAD_REQUEST);
    }
    
    /**
     * Job 삭제
     * @param jobRequest
     * @return
     */
    @PostMapping(value = "/deleteJob")
    public ResponseEntity<?> deleteScheduleJob(@RequestBody JobRequest  jobRequest) {
        log.info("delete schedule job :: jobRequest : {}", jobRequest);
        
        boolean result = false;
        String msg = null;
        String code = "1000";   // [?]은 필수 값 입니다.
        List<Object> codeParam = new ArrayList<Object>();
        
        if (jobRequest.getJobName() == null) {
            codeParam.add("jobName");
        } else {
            JobKey jobKey = new JobKey(jobRequest.getJobName(), jobRequest.getJobGroup());
            
            if (schedulerService.isJobExists(jobKey)) {
                if (!schedulerService.isJobRunning(jobKey)) {
                    schedulerService.deleteJob(jobKey);
                    result = true;
                    code = "200";   // 정상               
                } else {
                    code = "1005";  // 해당 작업이 실행중 입니다. 작업 종료 후 요청 하세요. [?]
                    codeParam.add(jobRequest.getJobName());
                }
            } else {
                code = "1004";  // 등록되지 않은 작업명 입니다. [?]
                codeParam.add(jobRequest.getJobName());
            }
        }
        
        Map<String, Object> resultMap = new HashMap<String, Object>();
        resultMap.put("code", code);
        resultMap.put("codeParam", codeParam.toArray());
        resultMap.put("result", result);
        resultMap.put("msg", msg);
        
        return new ResponseEntity<>(ApiResponse.createApiResponse(resultMap), result ? HttpStatus.OK : HttpStatus.BAD_REQUEST);
    }
    
    
    /**
     * Job 수정
     * @param jobRequest
     * @return
     */
    @PostMapping(value = "/updateJob")
    public ResponseEntity<?> updateScheduleJob(@RequestBody JobRequest  jobRequest) {
        log.info("update schedule job :: jobRequest : {}", jobRequest);
        
        boolean result = false;
        String msg = null;
        String code = "1000";   // [?]은 필수 값 입니다.
        List<Object> codeParam = new ArrayList<Object>();
        
        if (jobRequest.getJobName() == null) {
            codeParam.add("jobName");
        } else if (jobRequest.getCronExpression() == null) {
            codeParam.add("cronExpression");
        } else if (!CronExpression.isValidExpression(jobRequest.getCronExpression())) {
            code = "1002";  // 잘못된 표현식 입니다. [?]
            codeParam.add(jobRequest.getCronExpression());
        } else { 
            JobKey jobKey = new JobKey(jobRequest.getJobName(), jobRequest.getJobGroup());
            
            if (schedulerService.isJobExists(jobKey)) {
                if (!schedulerService.isJobRunning(jobKey)) {
                    schedulerService.updateJob(jobRequest);
                    result = true;
                    code = "200";
                } else {
                    code = "1005";  // 해당 작업이 실행중 입니다. 작업 종료 후 요청 하세요. [?]
                    codeParam.add(jobRequest.getJobName());
                }
            } else {
                code = "1004";  // 등록되지 않은 작업명 입니다. [?]
                codeParam.add(jobRequest.getJobName());
            }
        }
        
        Map<String, Object> resultMap = new HashMap<String, Object>();
        resultMap.put("code", code);
        resultMap.put("codeParam", codeParam.toArray());
        resultMap.put("result", result);
        resultMap.put("msg", msg);
        
        return new ResponseEntity<>(ApiResponse.createApiResponse(resultMap), result ? HttpStatus.OK : HttpStatus.BAD_REQUEST);
    }
    
    /**
     * 스케줄링 활성화 된 Job 스케줄링 멈춤
     * @param jobRequest
     * @return
     */
    @PostMapping(value = "/pauseJob")
    public ResponseEntity<?> pauseJob(@RequestBody JobRequest  jobRequest) {
        log.info("pause schedule job :: jobRequest : {}", jobRequest);
        
        boolean result = false;
        String msg = null;
        String code = "1000";   // [?]은 필수 값 입니다.
        List<Object> codeParam = new ArrayList<Object>();
        
        if (jobRequest.getJobName() == null) {
            codeParam.add("jobName");
        } else {
            JobKey jobKey = new JobKey(jobRequest.getJobName(), jobRequest.getJobGroup());
            if (schedulerService.isJobExists(jobKey)) {
                if (schedulerService.isJobRunning(jobKey)) {
                    schedulerService.pauseJob(jobKey);
                    result = true;
                    code = "200";
                } else {                    
                    code = "1006";  // 해당 작업이 미실행중 입니다. [?]
                    codeParam.add(jobRequest.getJobName());
                }
            } else {
                code = "1004";  // 등록되지 않은 작업명 입니다. [?]
                codeParam.add(jobRequest.getJobName());
            }
        }
        
        Map<String, Object> resultMap = new HashMap<String, Object>();
        resultMap.put("code", code);
        resultMap.put("codeParam", codeParam.toArray());
        resultMap.put("result", result);
        resultMap.put("msg", msg);
        
        return new ResponseEntity<>(ApiResponse.createApiResponse(resultMap), result ? HttpStatus.OK : HttpStatus.BAD_REQUEST);
    }
    
    /**
     * 스케줄링 비활성화 된 Job 스케줄링 실행
     * @param jobRequest
     * @return
     */
    @PostMapping(value = "/resumeJob")
    public ResponseEntity<?> resumeJob(@RequestBody JobRequest  jobRequest) {
        log.info("resume schedule job :: jobRequest : {}", jobRequest);
        
        boolean result = false;
        String msg = null;
        String code = "1000";   // [?]은 필수 값 입니다.
        List<Object> codeParam = new ArrayList<Object>();
        
        if (jobRequest.getJobName() == null) {
            codeParam.add("jobName");
        } else {
            JobKey jobKey = new JobKey(jobRequest.getJobName(), jobRequest.getJobGroup());
            if (schedulerService.isJobExists(jobKey)) {
                String jobState = schedulerService.getJobState(jobKey);
                if (jobState.equals("PAUSED")) {
                    schedulerService.resumeJob(jobKey);
                    result = true;
                    code = "200";
                } else {
                    code = "1007";  // 해당 작업이 활성화 상태 입니다. [?]
                    codeParam.add(jobRequest.getJobName());
                }
            } else {
                code = "1004";  // 등록되지 않은 작업명 입니다. [?]
                codeParam.add(jobRequest.getJobName());
            }
        }
        
        Map<String, Object> resultMap = new HashMap<String, Object>();
        resultMap.put("code", code);
        resultMap.put("codeParam", codeParam.toArray());
        resultMap.put("result", result);
        resultMap.put("msg", msg);
        
        return new ResponseEntity<>(ApiResponse.createApiResponse(resultMap), result ? HttpStatus.OK : HttpStatus.BAD_REQUEST);
    }
    
    /**
     * 등록된 Job 즉시 실행
     * @param jobRequest
     * @return
     */
    @PostMapping(value = "/executeJob")
    public ResponseEntity<?> executeJob(@RequestBody JobRequest  jobRequest) {
        log.info("execute schedule job :: jobRequest : {}", jobRequest);
        
        boolean result = false;
        String msg = null;
        String code = "1000";   // [?]은 필수 값 입니다.
        List<Object> codeParam = new ArrayList<Object>();
        
        if (jobRequest.getJobName() == null) {
            codeParam.add("jobName");
        } else {
            JobKey jobKey = new JobKey(jobRequest.getJobName(), jobRequest.getJobGroup());
            if (schedulerService.isJobExists(jobKey)) {
                if (!schedulerService.isJobRunning(jobKey)) {
                    schedulerService.startJob(jobKey);
                    result = true;
                    code = "200";
                } else {
                    code = "1005";  // 해당 작업이 실행중 입니다. 작업 종료 후 요청 하세요. [?]
                    codeParam.add(jobRequest.getJobName());
                }
            } else {
                code = "1004";  // 등록되지 않은 작업명 입니다. [?]
                codeParam.add(jobRequest.getJobName());
            }
        }
        
        Map<String, Object> resultMap = new HashMap<String, Object>();
        resultMap.put("code", code);
        resultMap.put("codeParam", codeParam.toArray());
        resultMap.put("result", result);
        resultMap.put("msg", msg);
        
        return new ResponseEntity<>(ApiResponse.createApiResponse(resultMap), result ? HttpStatus.OK : HttpStatus.BAD_REQUEST);
    }
    
    /**
     * 등록된 Job 즉시 종료
     * @param jobRequest
     * @return
     */
    @PostMapping(value = "/stopJob")
    public ResponseEntity<?> stopJob(@RequestBody JobRequest  jobRequest) {
        log.info("stop schedule job :: jobRequest : {}", jobRequest);
        
        boolean result = false;
        String msg = null;
        String code = "1000";   // [?]은 필수 값 입니다.
        List<Object> codeParam = new ArrayList<Object>();
        
        if (jobRequest.getJobName() == null) {
            codeParam.add("jobName");
        } else {
            JobKey jobKey = new JobKey(jobRequest.getJobName(), jobRequest.getJobGroup());
            if (schedulerService.isJobExists(jobKey)) {
                if (schedulerService.isJobRunning(jobKey)) {
                    schedulerService.stopJob(jobKey);
                    result = true;
                    code = "200";
                } else {
                    code = "1006";  // 해당 작업이 미실행중 입니다. [?]
                    codeParam.add(jobRequest.getJobName());
                }
            } else {
                code = "1004";  // 등록되지 않은 작업명 입니다. [?]
                codeParam.add(jobRequest.getJobName());
            }
        }
        
        Map<String, Object> resultMap = new HashMap<String, Object>();
        resultMap.put("code", code);
        resultMap.put("codeParam", codeParam.toArray());
        resultMap.put("result", result);
        resultMap.put("msg", msg);
        
        return new ResponseEntity<>(ApiResponse.createApiResponse(resultMap), result ? HttpStatus.OK : HttpStatus.BAD_REQUEST);
    }
}
