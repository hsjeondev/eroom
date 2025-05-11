package com.eroom;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import com.eroom.mail.repository.MailStatusRepository;
import com.eroom.project.repository.ProjectRepository;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableScheduling
@RequiredArgsConstructor
public class SchedulerConfig {
	
	private final ProjectRepository projectRepository;
	private final MailStatusRepository mailStatusRepository;
	@Scheduled(cron = "0 0 0 * * *", zone = "Asia/Seoul")
	public void updateProjectProceed() {
		
		LocalDate yesterday = LocalDate.now(ZoneId.of("Asia/Seoul")).minusDays(1);
		
		projectRepository.updateProceedByEndDate("완료", yesterday);
		
	}
	// 메일 휴지통
	@Scheduled(cron = "0 0 0 * * *", zone = "Asia/Seoul") 
	public void deleteOldMailTrash() {
	    LocalDateTime limitDate = LocalDateTime.now(ZoneId.of("Asia/Seoul")).minusDays(15);
	    mailStatusRepository.updateVisibilityOfOldTrash(limitDate);
	}
	@Scheduled(cron = "0 0 0 1 1 *", zone = "Asia/Seoul")
	public void runEveryYear() {
	    // System.out.println("새해 첫날 실행됨!");
	}
	@Scheduled(cron = "0 0 0 1 * *", zone = "Asia/Seoul")
	public void runEveryMonth() {
	    // System.out.println("매달 1일 00시에 실행됨!");
	}
 }
