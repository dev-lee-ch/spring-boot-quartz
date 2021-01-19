package kr.co.ensecure.scheduler;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * eNsecure Scheduler Web Application Main Class
 * 
 * @author lee.ch
 *
 */
@SpringBootApplication
public class EnsecureSchedulerApplication implements CommandLineRunner {    // 스프링 컨테이너 로딩 후 처리가 필요한 경우, CommandLineRunner를 implements 후  run 메소드에서 처리

	public static void main(String[] args) {
		SpringApplication.run(EnsecureSchedulerApplication.class, args);
	}

    @Override
    public void run(String... args) throws Exception {
        // TODO Auto-generated method stub
        
    }

}
