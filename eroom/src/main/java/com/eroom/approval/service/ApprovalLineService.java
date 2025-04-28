package com.eroom.approval.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.eroom.approval.dto.ApprovalLineDto;
import com.eroom.approval.entity.ApprovalLine;
import com.eroom.approval.repository.ApprovalLineRepository;
import com.eroom.approval.repository.ApprovalRepository;
import com.eroom.employee.service.EmployeeService;
import com.eroom.websocket.ApprovalWebSocketHandler;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ApprovalLineService {

	private final ApprovalLineRepository approvalLineRepository;
	private final ApprovalRepository approvalRepository;
	private final EmployeeService employeeService;
	private final ApprovalWebSocketHandler approvalWebSocketHandler;
	
	// 결재 번호로 결재 라인 조회
	public List<ApprovalLine> getApprovalLineByApprovalNo(Long approvalNo) {
		List<ApprovalLine> resultList = approvalLineRepository.findApprovalLines(approvalNo);
//		for (ApprovalLine app : resultList) {
//			System.out.println(app.getApprovalLineNo());
//		}
		return resultList;
	}

	
	// 회원 번호로 결재 라인 조회
	public List<ApprovalLine> getApprovalLineByEmployeeNo(Long employeeNo) {
		List<ApprovalLine> resultList = approvalLineRepository.findApprovalLinesByEmployee_EmployeeNo(employeeNo);
		return resultList;
	}

	// 결재라인 합의자라인의 승인,반려 처리
	@Transactional(rollbackFor = Exception.class)
	public int approvalLineApproveDeny(ApprovalLine approvalLine) {
		int result = 0;
		try {
			Long approvalNo = approvalLine.getApproval().getApprovalNo();
			Long employeeNo = approvalLine.getEmployee().getEmployeeNo();
			
//			System.out.println("결재 번호 : " + approvalNo);
//			System.out.println("로그인한 직원번호 나와야함 : " + employeeNo);
			
			ApprovalLine appLine = approvalLineRepository.findByApproval_ApprovalNoAndEmployee_EmployeeNo(approvalNo, employeeNo);
//			System.out.println("결재 번호 : " + appLine.getApproval().getApprovalNo());
//			System.out.println("로그인한 직원번호 나와야함 : " + employeeNo);
//			appLine.setApprovalLineSignedDate(LocalDateTime.now());
			appLine.setApprovalLineStatus(approvalLine.getApprovalLineStatus());
//			System.out.println("이거이거 A 맞아?" + appLine.getApprovalLineStatus());
			if (appLine != null) {
				appLine.setApprovalLineStatus(approvalLine.getApprovalLineStatus());
				approvalLineRepository.save(appLine);
				
				// 여기서 웹소켓 등장! 알림용
				// 내 다음 사람이 있으면 알림 주기
				List<ApprovalLine> appLineZeroList = approvalLineRepository.findApprovalLinesByStepEqualZero(approvalNo);
				List<ApprovalLine> appLineOverOneList = approvalLineRepository.findApprovalLinesByStepGreaterThanEqualOne(approvalNo);
				// 합의자 전부 승인A 했다면 결재자 반복문 돌려서 웹소켓 알림 띄우고 break 그런데 appLineZeroList 사이즈가 0이면 반복 돌리지 않음.
				int isAllAgree = 0;
				for(ApprovalLine appLineZero : appLineZeroList) {
					if(appLineZero.getApprovalLineStatus().equals("A")) {
						isAllAgree++;
					}
				}
				
				if(isAllAgree == appLineZeroList.size() && !appLineOverOneList.isEmpty()) {
					// 합의자 전부 합의A 했다면~ + 결재자가 있다면~
					// 그 외 조건들은 앞에서 이미 걸러짐
//					for(ApprovalLine appLineOne : appLineOverOneList) {
//						if(appLineOne.getApprovalLineStatus().equals("S")) {
//							// 웹소켓 입장
//							String message = "[결재] 결재 요청이 도착했습니다. 승인 또는 반려해주세요.";
//							approvalWebSocketHandler.sendApprovalNotification(appLineOne.getEmployee().getEmployeeNo(), message, appLineOne.getApproval());
//							break;
//						}
//					}
					approvalLineOverOneListAndWebSocketHandler(appLineOverOneList);
				} else if(appLineZeroList.isEmpty() && !appLineOverOneList.isEmpty()) {
					approvalLineOverOneListAndWebSocketHandler(appLineOverOneList);
				}
				
				
				
				
			}
			result = 1;
		} catch (Exception e) {
			e.printStackTrace();
			result = -1;
			throw e;
		}
		return result;
	}

	public Boolean isMyLineApproval(Long approval_no, Long employee_no) {
		Boolean isMyLineApproval = false;
		List<ApprovalLine> approvalLines = approvalLineRepository.findApprovalLines(approval_no);
		for (ApprovalLine app : approvalLines) {
			if (app.getEmployee().getEmployeeNo() == employee_no) {
				isMyLineApproval = true;
				break;
			}
		}
		return isMyLineApproval;
	}

	private void approvalLineOverOneListAndWebSocketHandler(List<ApprovalLine> appLineOverOneList) {
		for(ApprovalLine appLineOne : appLineOverOneList) {
			if(appLineOne.getApprovalLineStatus().equals("S")) {
				// 웹소켓 입장
				String message = "[결재] 결재 요청이 도착했습니다. 승인 또는 반려해주세요.";
				approvalWebSocketHandler.sendApprovalNotification(appLineOne.getEmployee().getEmployeeNo(), message, appLineOne.getApproval());
				break;
			}
		}
	}
	
}
