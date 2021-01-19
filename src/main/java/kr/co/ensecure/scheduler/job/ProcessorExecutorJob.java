package kr.co.ensecure.scheduler.job;

import java.io.File;
import java.io.IOException;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.InterruptableJob;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobKey;
import org.quartz.UnableToInterruptJobException;
import kr.co.ensecure.scheduler.base.util.DateFormatUtil;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@DisallowConcurrentExecution    // 하나의 Trigger안에서 중복 실행 방지 (ex. job trigger를 1분마다 실행으로 설정, 해당job이 1분이상소요, @DisallowConcurrentExecution 미 사용시 선행작업 종료여부와 상관 없이 1분 마다 실행, 사용시 실행된 job 종료 후  실행주기에 맞춰 실행)
public class ProcessorExecutorJob implements InterruptableJob  /*implements Job*/ {

    private Thread currentThread = null;
    private Process process = null;
    
    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        // 즉시 종료 처리를 위한 Thread Object 저장
        this.currentThread = Thread.currentThread();
        
        JobDataMap jobDataMap = context.getJobDetail().getJobDataMap();
        JobKey jobKey = context.getJobDetail().getKey();
        
        System.out.println("--------------------------START ProcessorExecutorJob---------------------------");
        System.out.println("[job ] : " + jobKey);
        System.out.println("[time] : " + DateFormatUtil.now(DateFormatUtil.DATETIME_PATTERN));
        System.out.println("[jobDataMap] : " + jobDataMap.getWrappedMap().toString());
        System.out.println("[Path] : " + jobDataMap.getString("path"));
        System.out.println("[File] : " + jobDataMap.getString("file"));
        System.out.println();
        
        try {
            String path = jobDataMap.getString("path");
            String file = jobDataMap.getString("file");
            
            if (path != null && file != null) {
                if (new File(jobDataMap.getString("path") + "/" +  jobDataMap.getString("file")).exists()) {
                    process = Runtime.getRuntime().exec(jobDataMap.getString("path") + "/" +  jobDataMap.getString("file"));
                    System.out.println("프로세스가 실행 되었습니다. [PID] : " + process.pid());
                    process.waitFor();
                    System.out.println("프로세스가 종료 되었습니다. " + process.exitValue());
                } else {
                    System.out.println("지정된 파일을 찾을 수 없습니다. " + "[File] : " + path + "/" + file );
                }                
            } else {
                System.out.println("지정된 실행 파일이 없습니다.");
            }
        } catch (IOException e) {
            System.err.println(e);
        } catch (InterruptedException e) {
            System.out.println("Job이 즉시 종료 되었습니다.");
            if (this.process != null && this.process.isAlive()) {
                this.process.destroy();
                System.out.println("프로세스가 즉시 종료 되었습니다.");
            }
        } finally {
            process = null;
        }
        
        System.out.println("--------------------------END   ProcessorExecutorJob---------------------------");
        System.out.println();
    }

    /**
     * Job 즉시 종료 요청 시 프로세스 kill, Thread 종료
     */
    @Override
    public void interrupt() throws UnableToInterruptJobException {
        if( this.currentThread != null ) {
            this.currentThread.interrupt();
        }
    }

}
