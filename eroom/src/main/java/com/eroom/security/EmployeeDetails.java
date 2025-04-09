package com.eroom.security;

import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.eroom.directory.entity.Employee;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class EmployeeDetails implements UserDetails {
	
private static final long serialVersionUID = 1L;
	
	private final Employee employee;
	
	public Employee getEmployee() {
		return employee;
	}

	// 사용자 권한 설정
	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
//		return List.of(new SimpleGrantedAuthority("user"));
		return null;		
	}
	
	// 사용자 비밀번호 반환
	@Override
	public String getPassword() {
		return employee.getEmployeePw();
	}

	// 사용자 이름 반환
	@Override
	public String getUsername() {
		// 이메일, 아이디, 아이피 등등 쓸 수 있음
		return employee.getEmployeeId();
	}
	
	// 계정 상태 관리
	// is~로 시작, 반환 boolean
	
	// 계정 만료 여부 반환 메소드
	// 임시 계정, 비활성화된 계정
	@Override
	public boolean isAccountNonExpired() {
		// 만약 만료 여부가 있으면 여기에 로직 작성
//		if(member.getExpired().equals("Y")) {
//			return false;
//		} else {
//			return true;
//		}
		return true;
	}
	
	// 계정 잠금 여부
	// 비밀번호 5번 틀리면 -> 10분간 로그인 금지
	@Override
	public boolean isAccountNonLocked() {
		// 사용할 수 있는지 판단 여부, 언제부터 잠금 되었는지 확인하는 컬럼이 있으면
//		if(member.getLockedDate() + 10 > 현재시간) {
//			return false;
//			// 추가로 잠금 컬럼 초기화
//		} else {
//			return true;
//		}
		return true;
	}
	
	// 패스워드 만료 여부
	// 6개월 지났음 -> 비밀번호 변경 요청
	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}
	
	// 계정 사용 가능 여부
	@Override
	public boolean isEnabled() {
		return true;
	}
}
