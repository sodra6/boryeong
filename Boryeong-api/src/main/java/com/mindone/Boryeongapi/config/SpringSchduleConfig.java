package com.mindone.Boryeongapi.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

@Configuration
public class SpringSchduleConfig implements SchedulingConfigurer{
	/**
	 * 스레드풀 10개로 지정
	 * 성능보고 설정 변경할 수도 있음.
	 */
	private final int POOL_SIZE = 10;	//test 후 재지정!
	@Override
	public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
		// 스레드 스케줄러
		ThreadPoolTaskScheduler threadPoolTaskScheduler = new ThreadPoolTaskScheduler();
		
		threadPoolTaskScheduler.setPoolSize(POOL_SIZE); // 풀사이즈 설정
		threadPoolTaskScheduler.setThreadNamePrefix("waternet-scheduled-task-pool-"); // 스레드명 프리픽스 지정
		threadPoolTaskScheduler.initialize(); // 초기화
		// 스레드 스케줄러 등록
		taskRegistrar.setTaskScheduler(threadPoolTaskScheduler);
	}

}