package com.eroom;

import java.time.LocalDate;
import java.time.ZoneId;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import com.eroom.project.repository.ProjectRepository;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableScheduling
@RequiredArgsConstructor
public class SchedulerConfig {
	
	private final ProjectRepository projectRepository;

	@Scheduled(cron = "0 0 0 * * *", zone = "Asia/Seoul")
	public void updateProjectProceed() {
		
		LocalDate yesterday = LocalDate.now(ZoneId.of("Asia/Seoul")).minusDays(1);
		
		projectRepository.updateProceedByEndDate("완료", yesterday);
		
	}
	
 }
