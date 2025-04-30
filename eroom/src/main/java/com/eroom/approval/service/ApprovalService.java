package com.eroom.approval.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.eroom.approval.dto.ApprovalDto;
import com.eroom.approval.dto.ApprovalRequestDto;
import com.eroom.approval.entity.Approval;
import com.eroom.approval.entity.ApprovalFormat;
import com.eroom.approval.entity.ApprovalLine;
import com.eroom.approval.repository.ApprovalLineRepository;
import com.eroom.approval.repository.ApprovalRepository;
import com.eroom.attendance.entity.AnnualLeave;
import com.eroom.attendance.repository.AnnualLeaveRepository;
import com.eroom.calendar.dto.CompanyCalendarDto;
import com.eroom.calendar.entity.CompanyCalendar;
import com.eroom.calendar.repository.CompanyCalendarRepository;
import com.eroom.drive.dto.DriveDto;
import com.eroom.drive.service.DriveService;
import com.eroom.employee.entity.Employee;
import com.eroom.employee.repository.EmployeeRepository;
import com.eroom.websocket.ApprovalWebSocketHandler;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ApprovalService {
	
	private final ApprovalRepository approvalRepository;
	private final EmployeeRepository employeeRepository;
	private final ApprovalLineRepository approvalLineRepository;
	private final AnnualLeaveRepository annualLeaveRepository;
	private final CompanyCalendarRepository companyRepository;
	private final DriveService driveService;
	private final ApprovalWebSocketHandler approvalWebSocketHandler;
	

	// 내가 올린 결재 리스트 조회 + 신청일 기준으로 최신순 정렬
	public List<Approval> getMyRequestedApprovals(Long employeeNo, String visible) {
		List<Approval> resultList = approvalRepository.findByEmployee_EmployeeNoAndApprovalVisibleYnOrderByApprovalRegDateDesc(employeeNo, visible);
		return resultList;
	}

	// 결재 생성
	@Transactional(rollbackFor = Exception.class)
	public int createApproval(ApprovalRequestDto dto, Long employeeNo) {
		try {
//			ObjectMapper objectMapper = new ObjectMapper();
//			String json = objectMapper .writeValueAsString(dto.getContent());
			
			
			if(dto.getEditApprovalNo() != null) {
				Approval temp = approvalRepository.findById(dto.getEditApprovalNo()).orElse(null);
				if(temp != null) {
					ApprovalDto approvalDto = new ApprovalDto().toDto(temp);
					// 새로 올리는 결재인 경우다. 그러니까 이전 결재 상태가 회수일 경우 visible N으로 변경
					if(temp.getApprovalStatus().equals("F")) {
						approvalDto.setApproval_visible_yn("N");
					} else {
						
					}
					approvalDto.setApproval_no(dto.getEditApprovalNo());
					approvalDto.setApproval_completed_date(LocalDateTime.now());
					temp = approvalDto.toEntity();
					approvalRepository.save(temp);
				}
			}
			
			Employee emp = employeeRepository.findById(employeeNo).orElse(null);
			
			Approval approval = Approval.builder()
					.employee(emp)
					.approvalTitle(dto.getTitle())
					.approvalFormat(ApprovalFormat.builder().approvalFormatNo(Long.valueOf(dto.getFormat_no())).build())
					.approvalContent(dto.getContent())
					.approvalStatus("S")
					.build();
			
			Approval approvalResult = approvalRepository.save(approval);
			
			
			
			// 기존 파일 확인용
			if(!dto.getApprovalAttachFileIds().isEmpty() && dto.getApprovalAttachFileIds() != null) {
				driveService.createNewFilesUseOldFiles(dto.getApprovalAttachFileIds(), approvalResult.getApprovalNo());
			}
			
	        // 새 파일 확인용
	        if (dto.getFiles() != null && !dto.getFiles().isEmpty()) {
                DriveDto driveDto = DriveDto.builder()
                					.uploaderNo(employeeNo)
                					.separatorCode("FL007")
                					.driveFiles(dto.getFiles())
                					.param1(approvalResult.getApprovalNo())
                					.build();
                driveService.uploadApprovalAttachFiles(driveDto, employeeNo);
	        }
			
			
			
			
			
			
			
			ApprovalLine approvalLine = null;
			Long approvalResultNo = approvalResult.getApprovalNo();
			String message = "";
			// 3. 참조자 저장
			// 없는 경우 예외처리
			for (int i = 0; i < dto.getRefererIds().size(); i++) {
				
						approvalLine = ApprovalLine.builder()
						.approval(approval)
						.approvalLineStatus("A")
						.employee(Employee.builder().employeeNo(dto.getRefererIds().get(i)).build())
						.approvalLineStep(-1) // 참조자 고정값 -1
						.build();
					approvalLineRepository.save(approvalLine);
					
					// 새 결재글 생성시 참조자들에게 알림
					// 웹소켓 알림 기능. 알림 받는 사람의 EmployeeNo를 보내준다.
					message = "[결재] 참조 알림이 도착했습니다.";
			        if(approvalResultNo != null) {
			        	approvalWebSocketHandler.sendApprovalNotification(dto.getRefererIds().get(i), message, approvalResult);
			        }
			}

			// 2. 합의자 저장
			// 없는 경우 예외처리
			for (int i = 0; i < dto.getAgreerIds().size(); i++) {
				
				approvalLine = ApprovalLine.builder()
						.approval(approval)
						.approvalLineStatus("S")
						.employee(Employee.builder().employeeNo(dto.getAgreerIds().get(i)).build())
						.approvalLineStep(0) // 합의자 고정값 0
						.build();
					approvalLineRepository.save(approvalLine);
					
					// 새 결재글 생성시 합의자들에게 알림
					// 웹소켓 알림 기능. 알림 받는 사람의 EmployeeNo를 보내준다.
					message = "[결재] 합의 요청이 도착했습니다. 확인해주세요.";
			        if(approvalResultNo != null) {
			        	approvalWebSocketHandler.sendApprovalNotification(dto.getAgreerIds().get(i), message, approvalResult);
			        }
			}
			// 1. 결재자 저장
			for (int i = 0; i < dto.getApproverIds().size(); i++) {
				
				approvalLine = ApprovalLine.builder()
						.approval(approval)
						.approvalLineStatus("S")
						.employee(Employee.builder().employeeNo(dto.getApproverIds().get(i)).build())
						.approvalLineStep(dto.getApproverSteps().get(i)) // 결재자 순서 step 1,2,3...
						.build();
					approvalLineRepository.save(approvalLine);
					if(dto.getAgreerIds().isEmpty() && i == 0) {
						message = "[결재] 결재 요청이 도착했습니다. 승인 또는 반려해주세요.";
				        if(approvalResultNo != null) {
				        	approvalWebSocketHandler.sendApprovalNotification(dto.getApproverIds().get(i), message, approvalResult);
				        }
					}
			}
			
			
			
			return 1;
		} catch (Exception e) {
			e.printStackTrace();
			return 0;
		}
	}

	// 결재 번호로 결재 조회
	public Approval selectApprovalByApprovalNo(Long approvalNo) {
		Approval approval = approvalRepository.findById(approvalNo).orElse(null);
		return approval;
	}

	// 결재 삭제
	@Transactional(rollbackFor = Exception.class)
	public int updateVisibleYn(Long approvalNo) {
		int result = 0;
		try {
			Approval approval = approvalRepository.findById(approvalNo).orElse(null);
			if (approval != null) {
				if (!approval.getApprovalVisibleYn().equals("N")) {
					approval.setApprovalVisibleYn("N");
					approvalRepository.save(approval);
				}
			}
			List<DriveDto> driveDtoList = driveService.findApprovalDriveFiles(approvalNo);
			List<Long> driveNoList = new ArrayList<Long>();
			for(DriveDto driveDto : driveDtoList) {
				driveNoList.add(driveDto.getDriveAttachNo());
			}
			driveService.bulkDeleteDriveFiles(driveNoList);
			result = 1;
		} catch (Exception e) {
			e.printStackTrace();
			result = 0;
		}
		return result;
	}

	// 결재 번호로 결재 리스트 조회
	public List<Approval> getApprovalListByApprovalNo(Long approvalNo, String visible) {
		List<Approval> resultList = approvalRepository.findByApprovalNoAndApprovalVisibleYnOrderByApprovalRegDateDesc(approvalNo, visible);
		return resultList;
	}

	// 결재 회수
	public int updateApprovalStatus(Long approvalNo) {
		int result = 0;
		try {
			Approval approval = approvalRepository.findById(approvalNo).orElse(null);
			if (approval != null) {
				if (!approval.getApprovalStatus().equals("F")) {
					approval.setApprovalStatus("F");
					approvalRepository.save(approval);
				}
				result = 1;
			}
		} catch (Exception e) {
			e.printStackTrace();
			result = 0;
		}
		return result;
	}

	public List<Approval> findAllApprovalsVisibleY(String visible) {
		List<Approval> approvals = approvalRepository.findAllByApprovalVisibleYn(visible);
		return approvals;
	}

	// 결재글의 결재자와 합의자의 승인,반려 처리
	@Transactional(rollbackFor = Exception.class)
	public int approvalApproveDeny(ApprovalLine approvalLine, Boolean isFinalApprovalLineisMe) {
		int result = 0;
		try {
			Approval approval = approvalRepository.findById(approvalLine.getApproval().getApprovalNo()).orElse(null);
			
			if (approval != null) {
				ApprovalDto approvalDto = new ApprovalDto().toDto(approval);
				approvalDto.setApproval_status(approvalLine.getApprovalLineStatus());
				approvalDto.setApproval_completed_date(LocalDateTime.now());
				approval = approvalDto.toEntity();
				if(isFinalApprovalLineisMe || approvalDto.getApproval_status().equals("D")) {
					// 내가 마지막 (결재자 || 합의자) 인 경우 || 결재가 진행 상태인경우
					Approval endApproval = approvalRepository.save(approval);
					String message = "";
					// 웹소켓 알림 기능. 기안자의 EmployeeNo를 보내준다.
					if(endApproval.getApprovalStatus().equals("A")) {
						message = "[결재] " + approval.getApprovalFormat().getApprovalFormatTitle() + "의 결재가 완료 되었습니다!";
					} else if(endApproval.getApprovalStatus().equals("D")) {
						message = "[결재] " + approval.getApprovalFormat().getApprovalFormatTitle() + "의 결재가 반려 되었습니다!";
					}
			        approvalWebSocketHandler.sendApprovalNotification(endApproval.getEmployee().getEmployeeNo(), message, endApproval);
					// 연차 관련 결재인가 판단
					Employee approvalEmployee = approval.getEmployee();
					AnnualLeave annualLeave = null;
//					AnnualLeaveDto annualLeaveDto = null;
//					System.out.println(endApproval.getApprovalStatus() + " : 이거 A여야해");
					if(endApproval.getApprovalStatus().equals("A") && (approval.getApprovalFormat().getApprovalFormatNo() == 7 || approval.getApprovalFormat().getApprovalFormatNo() ==  8)) {
						// 연차 관련 결재
						if(approvalEmployee != null) {
							// 연차일
							Map<String, String> approvalContent = approval.getApprovalContent();
							DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
							DateTimeFormatter dtfFull = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
							
							// 연차 정보 수정 + 캘린더 추가
							annualLeave = annualLeaveRepository.findByEmployee_EmployeeNo(approvalEmployee.getEmployeeNo());
							if(annualLeave != null) {
								Double annualLeaveUsed = annualLeave.getAnnualLeaveUsed();
//								annualLeaveDto = new AnnualLeaveDto().toDto(annualLeave);
								
								// 캘린더Dto 준비
								String approvalEmployeeTeam = approvalEmployee.getStructure().getCodeName();
								String approvalEmployeeName = approvalEmployee.getEmployeeName();
								String approvalEmployeePosition = approvalEmployee.getEmployeePosition();
								String calendarTitleEmployeeInfo = approvalEmployeeName + " " + approvalEmployeePosition + " ";
								CompanyCalendarDto companyCalendarDto = CompanyCalendarDto.builder()
										.employee_no(approvalEmployee.getEmployeeNo())
										.company_creator(approvalEmployee.getEmployeeId())
										.separator("A001")
										.visibleYn("Y")
										.build();
								CompanyCalendar companyCalendarEntity = null;
								String vacationStart = "";
								String vacationEnd = "";
								
								if(approval.getApprovalFormat().getApprovalFormatNo() == 7) {
									// 연차의 경우
									// 날짜값 가져와야함
									vacationStart = approvalContent.get("vacationStart");
									vacationEnd = approvalContent.get("vacationEnd");
									LocalDate vacationStartFormatted = LocalDate.parse(vacationStart, dtf);
									LocalDate vacationEndFormatted = LocalDate.parse(vacationEnd, dtf);
									Long diffDays = ChronoUnit.DAYS.between(vacationStartFormatted, vacationEndFormatted);
									annualLeaveUsed = annualLeaveUsed + (diffDays + 1);
									// 연차 정보 캘린더 기입
									vacationStart += " 09:00:00";
									vacationEnd += " 18:00:00";
									companyCalendarDto.setCompany_title(calendarTitleEmployeeInfo +  " 연차");
									companyCalendarDto.setCompany_content(approvalEmployeeName + "님의 연차입니다. 참고부탁드립니다.");
									
								} else if(approval.getApprovalFormat().getApprovalFormatNo() == 8) {
									// 반차의 경우
									String vacation = approvalContent.get("vacation");
									String delimeter = approvalContent.get("amPm");
//									LocalDate vacationStartFormatted = LocalDate.parse(vacation, dtf);
									annualLeaveUsed = annualLeaveUsed + 0.5;
									// 연차 정보 캘린더 기입
									// 오전, 오후 정보가 있어야함
									if("am".equals(delimeter)) {
										vacationStart = vacation + " 09:00:00";
										vacationEnd = vacation + " 12:00:00";
										companyCalendarDto.setCompany_title(calendarTitleEmployeeInfo + " 반차(오전)");
										companyCalendarDto.setCompany_content("["+approvalEmployeeTeam+"] " + approvalEmployeeName + "님의 오전 반차입니다. 참고부탁드립니다.");
									} else if("pm".equals(delimeter)) {
										vacationStart = vacation + " 12:00:00";
										vacationEnd = vacation + " 18:00:00";
										companyCalendarDto.setCompany_title(calendarTitleEmployeeInfo + " 반차(오후)");
										companyCalendarDto.setCompany_content("["+approvalEmployeeTeam+"] " + approvalEmployeeName + "님의 오후 반차입니다. 참고부탁드립니다.");
									}
									
									
								}
								// 연차 정보 수정
								annualLeave.setAnnualLeaveUsed(annualLeaveUsed);
								annualLeaveRepository.save(annualLeave);
								
								// 캘린더 연차,반차 일정 추가
								LocalDateTime vacationStartFullFormatted = LocalDateTime.parse(vacationStart, dtfFull);
								LocalDateTime vacationEndFullFormatted = LocalDateTime.parse(vacationEnd, dtfFull);
								companyCalendarDto.setCalendar_start_time(vacationStartFullFormatted);
								companyCalendarDto.setCalendar_end_time(vacationEndFullFormatted);
								companyCalendarEntity = companyCalendarDto.toEntity();
								companyRepository.save(companyCalendarEntity);
								
							}
							
							
							
						} else {
//							System.out.println("여기로 테스트");
						}
					
					}
					
                }
				result = 1;
			}
		} catch (Exception e) {
			e.printStackTrace();
			result = 0;
			throw e; // 이렇게 해줘야 트랙잭션 롤백 가능!! 
		}
		return result;
	}
	



}
